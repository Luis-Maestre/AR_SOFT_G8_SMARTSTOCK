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
Fase 2: Prototipo según la arquitectura (etapa actual)
   ↓
Fase 3: Funcionalidad completa
```

## Arquitectura

Monolito modular con arquitectura por capas:

| Capa | Tecnología |
|---|---|
| Interfaz de usuario | **JavaFX** (aplicación de escritorio) |
| Lógica de negocio | Java + Spring Boot |
| Modelos | Entidades JPA |
| Datos | PostgreSQL con Spring Data JPA |
| Infraestructura | Docker + GitHub |

El módulo de reposición inteligente usará Python y se conectará al
backend más adelante.

## Carpetas del repositorio

```
.
├── desktop/          # Aplicación de escritorio con JavaFX (en construcción)
├── backend/          # Servidor Spring Boot: entidades, repositorios y API
├── docs/
│   ├── ddl/          # Script SQL de la base de datos
│   ├── diagramas/    # Diagramas del proyecto
│   └── maqueta-ui/   # Maqueta web de las pantallas (solo referencia de diseño)
└── docker-compose.yml
```

## Estado actual

| Parte | Estado |
|---|---|
| `docs/maqueta-ui/` | Listo: maqueta de 8 pantallas, solo como guía de diseño |
| `desktop/` | En construcción: aplicación JavaFX |
| `backend/` | Esqueleto: entidades, repositorios y servicios básicos, sin lógica de negocio |
| `docs/ddl/` | Listo: script SQL con las 11 tablas |
| `docs/diagramas/` | Pendiente: subir los diagramas de la Fase 1 |

## Equipo

- Omar Santiago Hernández Lamprea
- Luis Santo Padilla Maestre
- Tomás Estrada Martínez

Profesor: Jamilton Fernando Benavides
