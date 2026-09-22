# SmartStock ERP

Sistema de gestión de inventario para droguerías de barrio, con control
por lote y fecha de vencimiento (política FEFO), alertas automáticas de
stock mínimo, sugerencias de reposición y módulo de auditoría.

Proyecto desarrollado para la asignatura **Arquitectura de Software**,
Ingeniería de Software, Universidad Manuela Beltrán.

## Trazabilidad del proyecto

```
Fase 1: Idea y propuesta inicial
   ↓
Fase 2: Prototipo estructurado según la arquitectura   ← estado actual
   ↓
Fases posteriores: implementación funcional
```

## Arquitectura seleccionada

Monolítica Modular + Arquitectura por Capas:

- **Presentación**: React + TypeScript (prototipado en Fase 2 con
  HTML/CSS/JS vanilla como aproximación rápida — ver `frontend/README.md`)
- **Aplicación y Lógica de Negocio**: Java + Spring Boot
- **Modelos**: entidades JPA compartidas entre los módulos de negocio
- **Persistencia**: PostgreSQL vía Spring Data JPA
- **Infraestructura**: Docker + GitHub

## Estructura del repositorio

```
.
├── frontend/         # Prototipo de interfaz (Fase 2 — funcional con datos de prueba)
├── backend/          # Esqueleto Spring Boot: entidades, repositorios, controladores REST
├── docs/
│   ├── ddl/           # Script SQL del modelo relacional
│   └── diagramas/      # Diagramas UML y entidad-relación del proyecto
└── docker-compose.yml # Orquestación de referencia (frontend + backend + PostgreSQL)
```

## Estado de cada módulo en esta fase

| Módulo | Estado en Fase 2 |
|---|---|
| `frontend/` | Funcional: 8 pantallas navegables, formularios operativos sobre datos de prueba en memoria |
| `backend/` | Esqueleto: entidades, repositorios y controladores REST creados, sin lógica de negocio ni conexión a base de datos real |
| `docs/` | Script DDL documentado; carpeta lista para alojar los diagramas UML sustentados |

## Cómo ejecutar lo que ya funciona

Por ahora solo el frontend es ejecutable de punta a punta:

```bash
# No requiere instalación
abrir frontend/index.html en el navegador
```

El backend (`backend/`) se compilará y conectará a base de datos a
partir de la Fase 3.

## Equipo

- Omar Santiago Hernández Lamprea
- Luis Santo Padilla Maestre
- Tomás Estrada Martínez

Curso: Ingeniería de Software, 5to semestre — Arquitectura de Software
Profesor: Jamilton Fernando Benavides
