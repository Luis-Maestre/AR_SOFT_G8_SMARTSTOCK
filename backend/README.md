# SmartStock ERP — Backend

Servidor en **Java + Spring Boot**. Guarda los datos y los entrega a la
aplicación de escritorio (JavaFX) por medio de una API.

## Estado actual

Es un esqueleto: ya existen las piezas pero todavía no hay lógica de negocio

```
backend/src/main/java/co/edu/umb/smartstock/
├── SmartStockApplication.java   # Arranque del servidor
├── model/        # 11 entidades
├── repository/   # Acceso a la base de datos
├── controller/   # 7 controladores de la API
└── service/      # Lógica de negocio (aun vacio)
```

## Pendiente

- Conectar la base de datos.
- Programar la política FEFO, las alertas y la auditoría.
- Conectar con el cliente de escritorio JavaFX (`desktop/`).
