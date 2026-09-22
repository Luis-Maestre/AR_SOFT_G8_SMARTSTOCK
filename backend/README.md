# SmartStock ERP — Backend (esqueleto Fase 2)

Estructura inicial del backend en **Java + Spring Boot**, organizada
según la arquitectura en capas definida en la sección 4 del documento
(Presentación / Aplicación y Lógica de Negocio / Modelos / Persistencia).

## Estado en esta fase

Este backend **no se ejecuta con conexión a base de datos real todavía**
(ver `application.properties`). Su propósito en la Fase 2 es únicamente
representar la organización del proyecto según la arquitectura
seleccionada:

```
backend/
└── src/main/java/co/edu/umb/smartstock/
    ├── SmartStockApplication.java   # Punto de entrada
    ├── model/          # Capa de Modelos — 10 entidades JPA (@Entity)
    ├── repository/     # Capa de Persistencia — interfaces Spring Data JPA
    ├── controller/     # Capa de Aplicación — 7 @RestController
    └── service/        # Lógica de negocio (vacío, se completa en Fase 3)
```

Las entidades en `model/` corresponden exactamente a las tablas del
modelo relacional (sección 6): `usuario`, `rol`, `producto`,
`categoria`, `inventario`, `lote`, `movimiento_inventario`, `alerta`,
`reposicion`, `proveedor`, `auditoria`.

Los controladores en `controller/` exponen endpoints REST base
(`GET`/`POST`) sin validaciones de negocio todavía — son el punto de
partida que en Fase 3 se conectará con el frontend React y con el
microservicio Python de reposición inteligente.

## Cómo se ejecutará (Fase 3)

```bash
cd backend
mvn spring-boot:run
```

Requiere Java 17+, Maven y una instancia de PostgreSQL configurada en
`application.properties`.
