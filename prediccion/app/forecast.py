"""Núcleo de predicción de demanda y recomendación de reposición.

Solo usa numpy y scikit-learn (sin FastAPI), para poder probarlo aislado.

Entrada por producto: historial de salidas (fecha, cantidad), existencia actual
y stock mínimo. Salida: demanda estimada, modelo usado y cantidad sugerida.
"""
from __future__ import annotations

import math
from dataclasses import dataclass, field
from datetime import date, timedelta
from typing import Callable, Dict, Iterable, List, Optional, Tuple

import numpy as np
from sklearn.linear_model import Ridge

MODELO_REGLA = "regla_minimo"
MODELO_MEDIA = "media_movil"
MODELO_SES = "suavizado_exponencial"
MODELO_LINEAL = "regresion_lineal"

MIN_DIAS_BACKTEST = 21   # con menos historia no se puede comparar modelos
MIN_DIAS_MODELO = 7      # con menos historia se usa la regla del mínimo


@dataclass
class Resultado:
    producto_id: int
    modelo: str
    demanda_diaria: float
    demanda_horizonte: float
    cantidad_sugerida: int
    mae: Optional[float]
    dias_historial: int
    motivo: str
    horizonte_dias: int
    candidatos: Dict[str, float] = field(default_factory=dict)


def serie_diaria(registros: Iterable[Tuple[date, float]], hasta: date) -> Tuple[np.ndarray, Optional[date]]:
    """Convierte (fecha, cantidad) en una serie diaria sin huecos (días sin venta = 0).

    Devuelve (serie, fecha_inicio). Ignora fechas posteriores a `hasta`
    y cantidades negativas. Si no hay datos, devuelve serie vacía.
    """
    por_dia: Dict[date, float] = {}
    for fecha, cantidad in registros:
        if fecha > hasta or cantidad is None or cantidad <= 0:
            continue
        por_dia[fecha] = por_dia.get(fecha, 0.0) + float(cantidad)
    if not por_dia:
        return np.zeros(0), None
    inicio = min(por_dia)
    n = (hasta - inicio).days + 1
    y = np.zeros(n)
    for fecha, cantidad in por_dia.items():
        y[(fecha - inicio).days] = cantidad
    return y, inicio


# ---- modelos: cada uno devuelve el pronóstico diario de los próximos h días ----

def _media_movil(y: np.ndarray, h: int, inicio_semana: int, ventana: int = 7) -> np.ndarray:
    v = min(ventana, len(y))
    return np.full(h, max(float(np.mean(y[-v:])), 0.0))


def _suavizado(y: np.ndarray, h: int, inicio_semana: int, alpha: float = 0.3) -> np.ndarray:
    nivel = float(y[0])
    for valor in y[1:]:
        nivel = alpha * float(valor) + (1 - alpha) * nivel
    return np.full(h, max(nivel, 0.0))


def _caracteristicas(indices: np.ndarray, dia_semana0: int, n_ref: int) -> np.ndarray:
    t = (indices / max(n_ref, 1)).reshape(-1, 1)
    dow = (indices + dia_semana0) % 7
    dummies = np.zeros((len(indices), 7))
    dummies[np.arange(len(indices)), dow.astype(int)] = 1.0
    return np.hstack([t, dummies])


def _lineal(y: np.ndarray, h: int, dia_semana0: int) -> np.ndarray:
    """Ridge con tendencia + día de la semana. dia_semana0 = día de la semana del índice 0."""
    n = len(y)
    x = _caracteristicas(np.arange(n), dia_semana0, n)
    modelo = Ridge(alpha=1.0).fit(x, y)
    futuro = _caracteristicas(np.arange(n, n + h), dia_semana0, n)
    return np.clip(modelo.predict(futuro), 0.0, None)


def _envolver(nombre: str) -> Callable[[np.ndarray, int, int], np.ndarray]:
    if nombre == MODELO_MEDIA:
        return _media_movil
    if nombre == MODELO_SES:
        return _suavizado
    return _lineal


MODELOS = [MODELO_MEDIA, MODELO_SES, MODELO_LINEAL]


def _backtest(y: np.ndarray, dia_semana0: int) -> Dict[str, float]:
    """MAE de cada modelo prediciendo los últimos días de la propia serie."""
    prueba = min(7, len(y) // 3)
    entrenamiento, real = y[:-prueba], y[-prueba:]
    maes: Dict[str, float] = {}
    for nombre in MODELOS:
        try:
            pred = _envolver(nombre)(entrenamiento, prueba, dia_semana0)
            maes[nombre] = float(np.mean(np.abs(pred - real)))
        except Exception:  # un modelo que falla no debe tumbar la predicción
            continue
    return maes


def predecir(
    producto_id: int,
    registros: List[Tuple[date, float]],
    existencia: int,
    stock_minimo: int,
    hoy: date,
    horizonte_dias: int = 14,
) -> Resultado:
    """Calcula la demanda esperada y la cantidad sugerida a reponer."""
    if horizonte_dias < 1:
        raise ValueError("horizonte_dias debe ser >= 1")
    existencia = max(int(existencia), 0)
    stock_minimo = max(int(stock_minimo), 0)

    y, inicio = serie_diaria(registros, hoy)
    n = len(y)

    if n < MIN_DIAS_MODELO or float(y.sum()) == 0.0:
        objetivo = 2 * stock_minimo
        sugerida = max(0, objetivo - existencia) if existencia <= stock_minimo else 0
        return Resultado(
            producto_id=producto_id, modelo=MODELO_REGLA, demanda_diaria=0.0, demanda_horizonte=0.0,
            cantidad_sugerida=int(sugerida), mae=None, dias_historial=n, horizonte_dias=horizonte_dias,
            motivo="Sin historial suficiente: se usa la regla del stock mínimo (objetivo = 2 x mínimo).",
        )

    dia_semana0 = inicio.weekday()
    # el pronóstico empieza mañana: el día n tiene weekday (dia_semana0 + n) % 7
    if n >= MIN_DIAS_BACKTEST:
        maes = _backtest(y, dia_semana0)
        elegido = min(maes, key=maes.get) if maes else MODELO_MEDIA
        mae: Optional[float] = maes.get(elegido)
        motivo = f"Modelo elegido por menor error en backtest ({elegido})."
    else:
        maes, elegido, mae = {}, MODELO_MEDIA, None
        motivo = "Historial corto (menos de 21 días): se usa media móvil."

    diario = _envolver(elegido)(y, horizonte_dias, dia_semana0)
    demanda_horizonte = float(np.sum(diario))
    sugerida = max(0, math.ceil(demanda_horizonte + stock_minimo - existencia - 1e-9))
    return Resultado(
        producto_id=producto_id, modelo=elegido,
        demanda_diaria=round(demanda_horizonte / horizonte_dias, 4),
        demanda_horizonte=round(demanda_horizonte, 2),
        cantidad_sugerida=int(sugerida),
        mae=None if mae is None else round(mae, 4),
        dias_historial=n, horizonte_dias=horizonte_dias, motivo=motivo,
        candidatos={k: round(v, 4) for k, v in maes.items()},
    )
