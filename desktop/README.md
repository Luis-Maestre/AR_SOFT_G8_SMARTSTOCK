# SmartStock ERP — Aplicación de escritorio (JavaFX)

Es la interfaz que usará el personal de la droguería. Se instala y se
abre como un programa de escritorio, no en el navegador.

## Qué tiene hoy

- Pantalla de **inicio de sesión** (por ahora acepta cualquier dato).
- Pantalla principal con la tabla de **productos** y la tabla de **lotes ordenados por vencimiento (FEFO)**, con datos que vienen del servidor.

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

Si el servidor está apagado, la aplicación abre igual y muestra el aviso
"No se pudo conectar con el servidor". Al encenderlo, presionar **Actualizar**.

## Organización

```
src/main/java/co/edu/umb/smartstock/desktop/
├── MainApp.java        # Arranque
├── Navegador.java      # Cambia de pantalla
├── controller/         # Qué hace cada pantalla
├── model/              # Datos que se muestran (producto, categoría)
└── service/            # Comunicación con el servidor (ApiClient)
src/main/resources/
├── views/              # Diseño de las pantallas (.fxml)
└── css/                # Colores y estilos
```

Las pantallas de referencia están en `docs/maqueta-ui/`.
