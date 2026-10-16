# SmartStock ERP

Sistema de gestión de inventario para droguerías de barrio, con control
por lote y fecha de vencimiento (política FEFO), alertas de stock mínimo,
sugerencias de reposición y registro de auditoría.

Proyecto de la asignatura **Arquitectura de Software**, Ingeniería de
Software, Universidad Manuela Beltrán.

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

## Equipo

- Omar Santiago Hernández Lamprea
- Luis Santo Padilla Maestre
- Tomás Estrada Martínez

Profesor: Jamilton Fernando Benavides
