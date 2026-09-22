# SmartStock ERP — Frontend

Sistema de gestión de inventario para droguerías de barrio, desarrollado
en el marco de la asignatura Arquitectura de Software (UMB).

## Fase 2 — Prototipo estructurado

Este prototipo evoluciona la interfaz inicial de la Fase 1 hacia una
estructura más completa, organizada según el patrón **Modelo–Vista–
Controlador (MVC)** definido en la arquitectura del proyecto.

### Qué incluye esta fase

- **8 pantallas navegables**: login, dashboard, productos, inventario,
  movimientos, reposiciones, reportes y usuarios.
- **Navegación funcional** entre todas las pantallas mediante un menú
  lateral persistente.
- **Estructura MVC en el frontend**, como aproximación al patrón que se
  implementará con Spring Boot en el backend (Fase 3):
  - `js/models/` → clases que representan las entidades (Producto, Lote,
    Alerta, Usuario) con su lógica básica asociada (ej. cálculo de
    estado de stock, orden FEFO por fecha de vencimiento).
  - `js/views/` → funciones puras de renderizado (`render.js`) que
    pintan tablas, tarjetas y listas de alertas en el DOM, sin lógica
    de negocio.
  - `js/controllers/` → un controlador por módulo (`productoController`,
    `inventarioController`, `movimientoController`,
    `reposicionController`, `reporteController`, `usuarioController`,
    `authController`), que procesan eventos de la vista y coordinan
    datos del modelo — equivalentes en el frontend a los
    `@RestController` que existirán en el backend.
- **Datos de prueba** (`js/data/mock-data.js`) que simulan lo que en la
  Fase 3 vendrá de PostgreSQL vía la API REST: productos, lotes,
  inventario, movimientos, alertas, reposiciones, proveedores, usuarios
  y auditoría.
- **Formularios funcionales** (registrar producto, registrar
  movimiento, registrar usuario, confirmar reposición) que actualizan
  los datos en memoria y refrescan la vista al instante.
- **Política FEFO** aplicada visualmente: en la pantalla de Inventario,
  los lotes se ordenan automáticamente por fecha de vencimiento más
  próxima.

### Qué NO incluye todavía (según el alcance de esta fase)

- Conexión a base de datos real (PostgreSQL).
- Backend con Spring Boot / API REST.
- Autenticación real (el login acepta cualquier correo/contraseña).
- Persistencia de datos entre sesiones (los cambios se pierden al
  recargar la página).

### Estructura de carpetas

```
frontend/
├── index.html                      # Login (punto de entrada)
├── README.md
├── css/
│   └── styles.css                  # Sistema de diseño (tokens, componentes)
├── pages/
│   ├── dashboard.html
│   ├── productos.html
│   ├── inventario.html
│   ├── movimientos.html
│   ├── reposiciones.html
│   ├── reportes.html
│   └── usuarios.html
└── js/
    ├── data/
    │   └── mock-data.js            # Datos de prueba (simulan la BD)
    ├── models/                     # Capa Modelo
    │   ├── producto.js
    │   ├── lote.js
    │   ├── alerta.js
    │   └── usuario.js
    ├── views/                      # Capa Vista
    │   └── render.js
    └── controllers/                # Capa Controlador
        ├── authController.js
        ├── productoController.js
        ├── inventarioController.js
        ├── movimientoController.js
        ├── reposicionController.js
        ├── reporteController.js
        ├── usuarioController.js
        └── navController.js
```

### Cómo ejecutarlo

No requiere instalación ni dependencias. Basta con abrir
`frontend/index.html` en un navegador, o servirlo con una extensión
tipo *Live Server*.

### Próximos pasos (Fase 3)

- Migrar el frontend a **React + TypeScript**, según lo definido en la
  sección de arquitectura del documento.
- Implementar el backend en **Java + Spring Boot**, exponiendo los
  mismos módulos ya prototipados aquí como controladores REST
  (`ProductoController`, `InventarioController`, etc.).
- Conectar el modelo de datos a **PostgreSQL** siguiendo el script DDL
  definido en la sección de Modelo Relacional.
- Sustituir `mock-data.js` por llamadas reales a la API.

---
*Fase 1: Idea y propuesta inicial → **Fase 2: Prototipo estructurado
según la arquitectura** → Fase 3: Implementación funcional.*
