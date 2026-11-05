# SmartStock ERP — Aplicación de escritorio (JavaFX)

Interfaz para el personal de la droguería. Habla con el backend por API REST con el token de la sesión.

## Pantallas

Login · Dashboard · Productos (crear, editar, desactivar) · Categorías · Proveedores · Inventario ·
Movimientos · Reposiciones (generar sugerencias, confirmar, rechazar) · Reportes (CSV) ·
Usuarios y Auditoría (solo administrador).

El menú y los botones de escritura se ajustan al rol: el rol `usuario` es de solo lectura.

## Cómo abrirla

Requisitos: Java 17+ y Maven. Con el backend encendido (ver `backend/README.md`):

```bash
cd desktop
mvn javafx:run
```

Si el backend no está en `http://localhost:8080/api`, indica la dirección:

```bash
mvn javafx:run -Dsmartstock.api=https://mi-servidor.example.com/api
# o con la variable de entorno SMARTSTOCK_API_URL
```

## Organización

```
src/main/java/co/edu/umb/smartstock/desktop/
├── MainApp.java, Navegador.java
├── component/    # MenuLateral, TarjetaIndicador, FormularioProducto
├── controller/   # Un controlador por pantalla
├── model/Api.java        # Datos que entrega el servidor
├── service/      # ApiClient (HTTP + token) y Sesion (usuario actual)
└── util/         # Tablas, Avisos y Tareas (llamadas en segundo plano)
src/main/resources/ views/*.fxml y css/estilos.css
```

Para agregar una pantalla: crear su `.fxml` y su controlador y añadir una línea en `MenuLateral.OPCIONES`.
