# SmartStock ERP — Aplicación de escritorio (JavaFX)

Es la interfaz que usará el personal de la droguería. Se instala y se
abre como un programa de escritorio

## Funcionalidades que tiene hoy

- Pantalla de **inicio de sesión** sin validación momentanea
- Pantalla de **productos**

## Cómo abrirla

Requisitos: Java 17 o superior y Maven.

```bash
cd desktop
mvn javafx:run
```

## Organización

```
src/main/java/co/edu/umb/smartstock/desktop/
├── MainApp.java        # Arranque
├── Navegador.java      # Cambia de pantalla
├── controller/         # Qué hace cada pantalla
├── model/              # Datos que se muestran (producto, categoría)
└── service/            # Origen de los datos
src/main/resources/
├── views/              # Diseño de las pantallas (.fxml)
└── css/                # Colores y estilos
```

Las pantallas de referencia están en `docs/maqueta-ui/`.
