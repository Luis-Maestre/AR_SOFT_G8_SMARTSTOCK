# SmartStock ERP — Backend

Servidor en **Java + Spring Boot**. Guarda los datos y los entrega a la
aplicación de escritorio (JavaFX) por medio de una API.

## Cómo encenderlo

Requisitos: Java 17 o superior y Maven. No hace falta instalar ninguna
base de datos: usa una en memoria con datos de ejemplo.

```bash
cd backend
mvn spring-boot:run
```

Para comprobar que funciona, abrir en el navegador:

- http://localhost:8080/api/health → debe responder `"estado":"ok"`
- http://localhost:8080/api/productos → lista de productos de ejemplo

Los datos de ejemplo están en `src/main/resources/data.sql` y se
reinician cada vez que se apaga el servidor.

Para usar PostgreSQL en lugar de la base en memoria, ver
`src/main/resources/application-postgres.properties`.

## Organización

```
backend/src/main/java/co/edu/umb/smartstock/
├── SmartStockApplication.java   # Arranque del servidor
├── model/        # 11 entidades (Producto, Lote, Usuario, ...)
├── repository/   # Acceso a la base de datos
├── controller/   # Controladores de la API
└── service/      # Lógica de negocio (vacío por ahora)
```

## Pendiente

- Alertas de stock mínimo, auditoría y reposición.
- Inicio de sesión real.
