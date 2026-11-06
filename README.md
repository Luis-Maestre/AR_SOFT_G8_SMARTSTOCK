# SmartStock ERP

Sistema de gestión de inventario para droguerías de barrio, con alertas de
stock mínimo, **sugerencias de compra basadas en predicción de demanda**,
proveedores, reportes y registro de auditoría.

Proyecto de la asignatura **Arquitectura de Software**, Ingeniería de
Software, Universidad Manuela Beltrán.

## Arquitectura

Monolito modular por capas, más un servicio de predicción en Python:

```
 JavaFX (desktop)  ──REST/JWT──▶  Spring Boot (backend)  ──JDBC──▶  PostgreSQL en Supabase
                                        │
                                        └──HTTP──▶  FastAPI (prediccion): demanda y reposición
```

| Capa | Tecnología |
|---|---|
| Interfaz de usuario | JavaFX 21 |
| Lógica de negocio / API | Java 17 + Spring Boot 3.3 + Spring Security (JWT, BCrypt) |
| Datos | PostgreSQL (Supabase) con Spring Data JPA |
| Predicción de demanda | Python 3.12 + FastAPI + scikit-learn |
| Infraestructura | Docker + GitHub |

## Carpetas

```
.
├── desktop/       # App de escritorio JavaFX
├── backend/       # API REST Spring Boot
├── prediccion/    # Servicio Python de predicción y recomendación de reposición
├── docs/ddl/      # smartstock_schema.sql: crea las tablas en Supabase
├── docker-compose.yml
└── .env.example   # variables de entorno (copiar a .env, no subir)
```

## Qué incluye (según los requerimientos)

| RF | Funcionalidad |
|---|---|
| RF01 | Inicio de sesión real (JWT) con bloqueo tras 5 intentos fallidos |
| RF02 | Usuarios y roles: administrador, encargado de inventario, usuario |
| RF03 | CRUD de productos y categorías (baja lógica) |
| RF04 | Consulta de inventario con nivel de stock |
| RF05 | Movimientos de entrada, salida y ajuste (nunca deja el stock negativo) |
| RF06 | Alertas automáticas de stock mínimo (se resuelven solas al reponer) |
| RF07 | Proveedores |
| RF08–09 | Reposición: sugerencias calculadas por el módulo Python; el usuario confirma (con proveedor) o rechaza |
| RF10 | Dashboard y reportes con exportación a CSV (Excel) |
| RF11 | Auditoría de acciones (solo administrador) |

## Puesta en marcha

1. **Base de datos**: crear un proyecto en Supabase y ejecutar `docs/ddl/smartstock_schema.sql`
   en *SQL Editor*. Guía completa en [`backend/README.md`](backend/README.md).
2. **Variables**: `cp .env.example .env` y completar (conexión, `JWT_SECRET`, `ADMIN_PASSWORD`).
3. **Servicios**: `docker compose up --build` (levanta backend y predicción), o ver cada README para ejecutarlos sin Docker.
4. **App de escritorio**: `cd desktop && mvn javafx:run`.

Para probar sin nube ni Docker: `cd backend && mvn spring-boot:run` (perfil `dev`, base en memoria,
usuario `admin@smartstock.com` / `Admin123*`; solo para desarrollo).

## Equipo

- Omar Santiago Hernández Lamprea
- Luis Santo Padilla Maestre
- Tomás Estrada Martínez

Profesor: Jamilton Fernando Benavides
