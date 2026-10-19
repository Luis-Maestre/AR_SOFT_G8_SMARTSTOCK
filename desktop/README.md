# SmartStock ERP — Aplicación de escritorio (JavaFX)

Es la interfaz que usará el personal de la droguería. Se instala y se
abre como un programa de escritorio, no en el navegador.

## Qué tiene hoy (Actividad 5)

- Pantalla de **inicio de sesión** (por ahora acepta cualquier dato).
- **Marco principal** con barra superior y **menú lateral** para navegar entre las 7 pantallas.
- 7 pantallas: Dashboard, Productos, Inventario, Movimientos, Reposiciones, Reportes y Usuarios.
- **Productos** muestra datos reales que vienen del servidor (productos y lotes FEFO).
- Las demás pantallas funcionan en **modo demostración**, con datos de ejemplo en
  `service/DatosDemo.java`. En la Fase 3 se conectan al servidor con `ApiClient`.

## Cómo abrirla

Requisitos: Java 17 o superior y Maven.

1. Encender primero el servidor (ver `backend/README.md`):
   ```bash
   cd backend
   mvn spring-boot:run
   ```
2. En otra terminal, abrir la aplicación:
   ```bash
   cd desktop
   mvn javafx:run
   ```

Si el servidor está apagado, la aplicación abre igual; solo la pantalla
Productos muestra el aviso "No se pudo conectar con el servidor". Al
encenderlo, presionar **Actualizar**.

## Organización

```
src/main/java/co/edu/umb/smartstock/desktop/
├── MainApp.java        # Arranque
├── Navegador.java      # Cambia entre login y marco principal
├── component/          # Piezas visuales reutilizables
│   ├── MenuLateral.java        # Menú de navegación
│   └── TarjetaIndicador.java   # Tarjeta con número (dashboard)
├── controller/         # Qué hace cada pantalla
├── model/              # Datos que se muestran en las tablas
├── service/            # ApiClient (servidor) y DatosDemo (datos de ejemplo)
└── util/               # Ayudantes: llenar tablas (Tablas) y mensajes (Avisos)
src/main/resources/
├── views/              # Diseño de las pantallas (.fxml)
└── css/                # Colores y estilos
```

### Cómo funciona la navegación

`login.fxml` → `principal.fxml` (marco). El marco (`PrincipalController`) pone el
`MenuLateral` a la izquierda y carga en el centro el `.fxml` de la opción elegida.
Para agregar una pantalla nueva: crear su `.fxml` y su controlador, y añadir una
línea en `MenuLateral.OPCIONES`.

## Relación con los requerimientos y la arquitectura

Esta aplicación corresponde a la capa de **interfaz de usuario** de la
arquitectura por capas. No contiene lógica de negocio: en la Fase 3 las
pantallas pedirán todo al backend por API REST.

| Requerimiento | Pantalla | Vista (`views/`) | Controlador |
|---|---|---|---|
| RF01 Iniciar sesión | Login | `login.fxml` | `LoginController` |
| RF02 Usuarios y roles | Usuarios | `usuarios.fxml` | `UsuarioController` |
| RF03 Productos | Productos | `productos.fxml` | `ProductoController` |
| RF04 Consultar inventario | Inventario | `inventario.fxml` | `InventarioController` |
| RF05 Movimientos | Movimientos | `movimientos.fxml` | `MovimientoController` |
| RF06 Alertas de stock mínimo | Dashboard / Inventario | `dashboard.fxml`, `inventario.fxml` | `DashboardController`, `InventarioController` |
| RF08 y RF09 Reposiciones | Reposiciones | `reposiciones.fxml` | `ReposicionController` |
| RF10 Dashboard y reportes | Dashboard y Reportes | `dashboard.fxml`, `reportes.fxml` | `DashboardController`, `ReporteController` |

Pendiente para la Fase 3: login real, conectar las pantallas en modo
demostración al servidor, proveedores (RF07) y auditoría (RF11).

Las pantallas de referencia están en `docs/maqueta-ui/`.
