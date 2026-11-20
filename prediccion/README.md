# SmartStock — Servicio de predicción y reposición (Python)

Microservicio FastAPI que calcula la **demanda esperada** de cada producto y la **cantidad sugerida a comprar**.
El backend lo llama en `POST /reposiciones/generar`.

## Cómo calcula

1. Con el historial de salidas arma una serie diaria (los días sin venta cuentan 0).
2. Sin historial suficiente (menos de 7 días o sin ventas) usa la regla `regla_minimo`: si la existencia ≤ mínimo, sugiere reponer hasta `2 × stock mínimo`.
3. Con 7–20 días usa `media_movil` (promedio de los últimos 7 días).
4. Con 21 días o más compara tres modelos con un backtest (predice los últimos días conocidos y mide el error MAE) y usa el mejor:
   `media_movil`, `suavizado_exponencial` y `regresion_lineal` (Ridge con tendencia + día de la semana).
5. `cantidad sugerida = max(0, ceil(demanda del horizonte + stock mínimo − existencia))`. Horizonte por defecto: 14 días.

## Ejecutar

```bash
cd prediccion
python -m venv .venv && source .venv/bin/activate     # en Windows: .venv\Scripts\activate
pip install -r requirements.txt
uvicorn app.main:app --port 8000
```

Con Docker: `docker build -t smartstock-prediccion . && docker run -p 8000:8000 smartstock-prediccion`.

Si defines `PREDICTION_API_KEY`, el servicio exige el encabezado `X-API-Key` (usa el mismo valor en el backend).

## API

`GET /health` → `{"estado":"ok"}`

`POST /predict`
```json
{"horizonteDias": 14,
 "productos": [{"productoId": 1, "existencia": 20, "stockMinimo": 30,
                "historial": [{"fecha": "2026-10-01", "cantidad": 4}]}]}
```
Respuesta: `predicciones[]` con `productoId, modelo, demandaDiaria, demandaHorizonte, cantidadSugerida, mae, diasHistorial, motivo`.

## Pruebas

```bash
python -m unittest discover -s tests -t .      # núcleo de predicción (sin dependencias extra)
pip install -r requirements-dev.txt && pytest  # incluye las pruebas de la API
```
