# SmartStock ERP

<<<<<<< HEAD
Sistema de gestión de inventario para droguerías de barrio, con alertas de
stock mínimo, **sugerencias de compra basadas en predicción de demanda**,
proveedores, reportes y registro de auditoría.
=======
Sistema de gestión de inventario para droguerías de barrio, con control
por lote y fecha de vencimiento (política FEFO), alertas de stock mínimo,
sugerencias de reposición y registro de auditoría.
>>>>>>> d73906b8395de950b3b26a93762371c581175868

Proyecto de la asignatura **Arquitectura de Software**, Ingeniería de
Software, Universidad Manuela Beltrán.

<<<<<<< HEAD
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
=======
## Etapas del proyecto

```
Fase 1: Idea y propuesta inicial
   ↓
Fase 2 actual: Prototipo segun la arquitectura
   ↓
Fase 3: Funcionalidad completa
```

## Arquitectura

Monolito modular con arquitectura por capas:

| Capa | Tecnología |
|---|---|
| Interfaz de usuario | **JavaFX** |
| Lógica de negocio | Java + Spring Boot |
| Modelos | Entidades JPA |
| Datos | PostgreSQL con Spring Data JPA |
| Infraestructura | Docker + GitHub |

El módulo de reposición inteligente usará Python y se conectará al
backend más adelante

## Carpetas del repositorio

```
.
├── desktop/          # Aplicación de escritorio con JavaFX
├── backend/          # Servidor Spring Boot: entidades, repositorios y API
├── docs/
│   ├── ddl/          # Script SQL de la base de datos
│   ├── diagramas/    # Diagramas del proyecto
│   └── maqueta-ui/   # Maqueta web de las pantallas (solo referencia de diseño)
└── docker-compose.yml
```

## Como probarlo

Necesitas Java 17 o superior y Maven. Abrir dos terminales:

```bash
# 1) Servidor
cd backend
mvn spring-boot:run

# 2) Aplicación de escritorio
cd desktop
mvn javafx:run
```

En la pantalla de login escribe cualquier correo y contraseña para demostración

## Estado actual

| Parte | Estado |
|---|---|
| `docs/maqueta-ui/` | Listo: maqueta de 8 pantallas, solo como guía de diseño |
| `desktop/` | Funciona: login y pantalla con productos y lotes FEFO, datos tomados del servidor |
| `backend/` | Funciona: arranca con base en memoria, lista productos y lotes FEFO. Faltan alertas, auditoría y reposición |
| `docs/ddl/` | Listo: script SQL con las 11 tablas |
>>>>>>> d73906b8395de950b3b26a93762371c581175868

## Equipo

- Omar Santiago Hernández Lamprea
- Luis Santo Padilla Maestre
- Tomás Estrada Martínez

Profesor: Jamilton Fernando Benavides
