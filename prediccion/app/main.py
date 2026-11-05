from __future__ import annotations

import os
from datetime import date
from typing import List, Optional

from fastapi import Depends, FastAPI, Header, HTTPException
from pydantic import BaseModel, Field

from .forecast import predecir

app = FastAPI(title="SmartStock - Servicio de predicción", version="1.0.0")


class Venta(BaseModel):
    fecha: date
    cantidad: float = Field(ge=0)


class ProductoEntrada(BaseModel):
    productoId: int
    existencia: int = Field(ge=0)
    stockMinimo: int = Field(ge=0)
    historial: List[Venta] = []


class PeticionPrediccion(BaseModel):
    productos: List[ProductoEntrada] = Field(max_length=1000)
    horizonteDias: int = Field(default=14, ge=1, le=90)
    fechaReferencia: Optional[date] = None


class PrediccionSalida(BaseModel):
    productoId: int
    modelo: str
    demandaDiaria: float
    demandaHorizonte: float
    cantidadSugerida: int
    mae: Optional[float]
    diasHistorial: int
    horizonteDias: int
    motivo: str


class RespuestaPrediccion(BaseModel):
    predicciones: List[PrediccionSalida]


def verificar_clave(x_api_key: Optional[str] = Header(default=None)) -> None:
    esperada = os.getenv("PREDICTION_API_KEY", "")
    if esperada and x_api_key != esperada:
        raise HTTPException(status_code=401, detail="API key inválida")


@app.get("/health")
def health() -> dict:
    return {"estado": "ok"}


@app.post("/predict", response_model=RespuestaPrediccion, dependencies=[Depends(verificar_clave)])
def predict(peticion: PeticionPrediccion) -> RespuestaPrediccion:
    hoy = peticion.fechaReferencia or date.today()
    salida: List[PrediccionSalida] = []
    for p in peticion.productos:
        r = predecir(
            producto_id=p.productoId,
            registros=[(v.fecha, v.cantidad) for v in p.historial],
            existencia=p.existencia,
            stock_minimo=p.stockMinimo,
            hoy=hoy,
            horizonte_dias=peticion.horizonteDias,
        )
        salida.append(PrediccionSalida(
            productoId=r.producto_id, modelo=r.modelo, demandaDiaria=r.demanda_diaria,
            demandaHorizonte=r.demanda_horizonte, cantidadSugerida=r.cantidad_sugerida,
            mae=r.mae, diasHistorial=r.dias_historial, horizonteDias=r.horizonte_dias, motivo=r.motivo,
        ))
    return RespuestaPrediccion(predicciones=salida)
