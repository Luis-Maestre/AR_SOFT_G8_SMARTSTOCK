# SmartStock ERP

Sistema integral de planificación de recursos empresariales (ERP) para la gestión de inventario, con arquitectura dividida en tres componentes: un backend en Spring Boot, una API de predicción en Python y un cliente de escritorio en JavaFX.

## Requisitos Previos

Asegúrate de tener instalado lo siguiente antes de levantar el entorno:
- [Java Development Kit (JDK)](https://adoptium.net/) (Compatible con tu versión de Maven/JavaFX)
- [Maven](https://maven.apache.org/)
- [Python 3.8+](https://www.python.org/)
- Cuenta en [Supabase](https://supabase.com/) para la base de datos en la nube.

---

## Configuración del Entorno

### 1. Base de Datos (Supabase)
El proyecto utiliza Supabase como motor de base de datos relacional. Para inicializar las tablas:
1. Ve al **SQL Editor** de tu proyecto en Supabase.
2. Copia, pega y ejecuta el contenido del archivo `docs/ddl/smartstock_schema.sql`.
> **Nota:** Si tenías una versión anterior de la base de datos, asegúrate de hacer un `DROP TABLE ... CASCADE` de las tablas viejas o usa un proyecto nuevo, ya que el esquema actual utiliza `BIGINT` para los identificadores.

### 2. Variables de Entorno
Configura tus credenciales en tu archivo `.env` en la raíz de los componentes que lo requieran (asegúrate de que esté en tu `.gitignore` para proteger tus datos). 
Asegúrate de incluir las credenciales de tu base de datos de Supabase, tu `JWT_SECRET` (usa una cadena segura) y las variables `ADMIN_EMAIL` / `ADMIN_PASSWORD`.

---

## Ejecución del Proyecto

El sistema requiere que los tres servicios estén corriendo en paralelo. Abre tres terminales distintas y ejecuta los siguientes comandos:

### Servicio de Predicción (Python)
Levanta la API encargada de los cálculos y predicciones:
```bash
cd prediccion
python -m venv .venv

# Activar entorno (En Windows)
.venv\Scripts\activate
# En Linux/Mac usa: source .venv/bin/activate

pip install -r requirements.txt
uvicorn app.main:app --port 8000
```

### Backend (Spring Boot)
Levanta la API principal asegurándote de cargar las variables de tu `.env`:
```bash
cd backend

# Para ejecutar usando la base de datos en Supabase:
mvn spring-boot:run -Dspring-boot.run.profiles=supabase
```

### Aplicación de Escritorio (JavaFX)
Finalmente, inicia la interfaz de usuario:
```bash
cd desktop
mvn javafx:run
```

---

## Pruebas y Uso de la App

Una vez que la aplicación de escritorio esté corriendo, puedes iniciar sesión con las credenciales que definiste en tus variables de entorno (`ADMIN_EMAIL` / `ADMIN_PASSWORD`). 

> **Tip de desarrollo:** Si ejecutas el backend con el perfil `dev` (sin Supabase, usando la base de datos en memoria H2), puedes ingresar rápidamente con `admin@smartstock.com` / `Admin123*`.

**Flujo de prueba recomendado:**
1. Crea un producto nuevo.
2. Registra una entrada de inventario.
3. Registra una salida que deje el stock por debajo del mínimo establecido.
4. Verifica que aparezca la alerta en el sistema.
5. Usa la opción de *Generar sugerencias* y confirma la orden con el proveedor.

### Tests automatizados
Para correr las suites de pruebas de los distintos módulos:
- **Backend:** `cd backend && mvn test` (Ejecuta pruebas usando la BD en memoria H2).
- **Predicción:** `cd prediccion && python -m unittest discover -s tests -t .`