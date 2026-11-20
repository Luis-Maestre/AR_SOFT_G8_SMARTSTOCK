# SmartStock ERP

Sistema de gestión de inventario para droguerías de barrio: alertas de stock mínimo, **sugerencias de compra basadas en predicción de demanda**, proveedores, reportes y auditoría.

Proyecto de la asignatura **Arquitectura de Software**, Ingeniería de Software, Universidad Manuela Beltrán (UMB).

## Contenido

1. [Arquitectura](#arquitectura)
2. [Funcionalidades](#funcionalidades)
3. [Requisitos previos](#requisitos-previos)
4. [Configuración](#configuración)
5. [Cómo ejecutarlo](#cómo-ejecutarlo)
6. [Roles y permisos](#roles-y-permisos)
7. [Predicción y reposición](#predicción-y-reposición)
8. [Pruebas](#pruebas)
9. [Solución de problemas](#solución-de-problemas)
10. [Seguridad](#seguridad)
11. [Estructura del repositorio](#estructura-del-repositorio)
12. [Equipo](#equipo)

---

## Arquitectura

Monolito modular por capas, más un servicio de predicción en Python:

```
 JavaFX (desktop)  ──REST/JWT──▶  Spring Boot (backend)  ──JDBC──▶  PostgreSQL en Supabase
                                        │
                                        └──HTTP──▶  FastAPI (prediccion): demanda y reposición
```

| Capa | Tecnología | Puerto |
|---|---|---|
| Interfaz de usuario | JavaFX 21 | — |
| Lógica de negocio / API | Java 17 + Spring Boot 3.3 + Spring Security (JWT, BCrypt) | 8080 |
| Datos | PostgreSQL (Supabase) con Spring Data JPA | — |
| Predicción de demanda | Python + FastAPI + scikit-learn | 8000 |
| Infraestructura | Docker + GitHub | — |

## Funcionalidades

| RF | Funcionalidad |
|---|---|
| RF01 | Inicio de sesión con JWT. Tras 5 intentos fallidos se bloquea el acceso 5 minutos |
| RF02 | Usuarios y roles: administrador, encargado de inventario, usuario |
| RF03 | CRUD de productos y categorías (baja lógica) |
| RF04 | Consulta de inventario con nivel de stock |
| RF05 | Movimientos de entrada, salida y ajuste (nunca deja el stock negativo) |
| RF06 | Alertas automáticas de stock mínimo (se resuelven solas al reponer) |
| RF07 | Proveedores |
| RF08–09 | Reposición: sugerencias calculadas por el módulo Python; el usuario las confirma (con proveedor) o las rechaza |
| RF10 | Dashboard y reportes con exportación a CSV (se abre en Excel) |
| RF11 | Auditoría de acciones (solo administrador) |

## Requisitos previos

| Herramienta | Versión | Para qué |
|---|---|---|
| JDK | 17 o superior | Backend y escritorio |
| Maven | 3.9 o superior | Compilar y ejecutar backend y escritorio |
| Python | 3.10 o superior (recomendado 3.12, la del Docker) | Servicio de predicción |
| Docker Desktop | Opcional | Levantar backend y predicción con un solo comando |
| Cuenta de Supabase | — | Base de datos en la nube (no hace falta para el perfil `dev`) |

Verifica las versiones:

```bash
java -version
mvn -version
python --version
```

## Configuración

### 1. Base de datos (Supabase)

1. Crea un proyecto en [supabase.com](https://supabase.com) y guarda la contraseña de la base de datos.
2. Abre **SQL Editor → New query**, pega el contenido de `docs/ddl/smartstock_schema.sql` y ejecútalo. Crea las tablas, los 3 roles y activa RLS (la clave pública de Supabase no podrá leer tus datos).
3. Copia los datos de conexión desde **Connect → Session pooler**. El host depende de la región de tu proyecto, así que cópialo exactamente como aparece. Usa el puerto **5432**. No uses el puerto 6543 (transaction pooler) con este proyecto.

> El backend arranca con `ddl-auto=validate`: no crea ni modifica tablas. Si el esquema no coincide con las entidades, falla con un mensaje como `missing table [...]`. Si ya tenías una versión anterior de la base, usa un proyecto nuevo o borra las tablas viejas, porque el esquema actual usa `BIGINT` en los identificadores.

### 2. Variables de entorno

```bash
cp .env.example .env        # en Windows: copy .env.example .env
```

Completa los valores del archivo `.env`. **Nunca lo subas a Git** (ya está en `.gitignore`).

| Variable | Obligatoria | Por defecto | Descripción |
|---|---|---|---|
| `SUPABASE_DB_URL` | Sí (perfil `supabase`) | — | URL JDBC. Debe empezar por `jdbc:postgresql://` |
| `SUPABASE_DB_USER` | Sí (perfil `supabase`) | — | Usuario, con la forma `postgres.<ref-del-proyecto>` |
| `SUPABASE_DB_PASSWORD` | Sí (perfil `supabase`) | — | Contraseña de la base de datos |
| `JWT_SECRET` | Sí (perfil `supabase`) | solo en `dev` | Clave de firma de los tokens, de **32 caracteres o más** |
| `JWT_EXPIRACION_MIN` | No | `480` | Minutos de vigencia del token |
| `ADMIN_EMAIL` | No | `admin@smartstock.com` | Correo del administrador inicial |
| `ADMIN_PASSWORD` | Solo en el primer arranque | — | Contraseña del administrador inicial (mínimo 8 caracteres) |
| `ADMIN_NAME` | No | `Administrador` | Nombre del administrador inicial |
| `PREDICTION_URL` | No | `http://localhost:8000` | Dirección del servicio de predicción |
| `PREDICTION_API_KEY` | No | vacío | Si se define, debe tener el **mismo valor** en backend y predicción |
| `PREDICTION_HORIZON_DAYS` | No | `14` | Días que se proyectan para calcular la reposición |
| `PREDICTION_HISTORY_DAYS` | No | `90` | Días de historial de salidas que se envían al modelo |
| `PORT` | No | `8080` | Puerto del backend |
| `SMARTSTOCK_API_URL` | No | `http://localhost:8080/api` | Dirección del backend para la app de escritorio |

Genera un `JWT_SECRET` seguro:

```bash
openssl rand -base64 48
```

En Windows sin `openssl`, usa PowerShell:

```powershell
[Convert]::ToBase64String((1..48 | ForEach-Object { Get-Random -Maximum 256 }))
```

> **Importante:** Spring Boot **no lee el archivo `.env` por sí solo**. Lo cargan `SmartStock.bat` y Docker Compose. Si ejecutas el backend a mano, debes definir las variables en la terminal (ver más abajo).

## Cómo ejecutarlo

Hay cuatro formas. Elige una.

| Opción | Cuándo usarla | Necesita Supabase |
|---|---|---|
| A. Perfil `dev` | Probar rápido, sin instalar nada más | No |
| B. `SmartStock.bat` | Windows, todo con un doble clic | Sí |
| C. Manual (3 terminales) | Desarrollo y depuración | Sí |
| D. Docker Compose | Backend y predicción en contenedores | Sí |

El orden de arranque es siempre: **predicción → backend → escritorio**.

### A. Modo rápido (`dev`)

Usa una base H2 en memoria con datos de ejemplo. Los datos se pierden al apagar el servidor.

```bash
cd backend
mvn spring-boot:run
```

Usuario: `admin@smartstock.com` / `Admin123*`. **Solo para desarrollo.** Para probar las sugerencias de reposición, levanta también el servicio de predicción (opción C, paso 1).

### B. `SmartStock.bat` (Windows)

Requisitos: archivo `.env` en la raíz y el entorno virtual de Python ya creado (`prediccion\.venv`, ver opción C, paso 1).

Doble clic en `SmartStock.bat`, o desde la raíz del proyecto:

```bat
SmartStock.bat
```

El script carga el `.env`, activa el perfil `supabase`, compila el backend la primera vez, inicia la predicción y el backend en ventanas minimizadas, espera a que `/api/health` responda (hasta 2 minutos), abre la aplicación de escritorio y, al cerrarla, detiene los servicios.

### C. Manual (tres terminales)

**Primero, carga las variables** en cada terminal donde vayas a correr backend o predicción (PowerShell, estando en la raíz del proyecto):

```powershell
Get-Content .env | Where-Object { $_ -match '^\s*[^#\s]\S*=' } | ForEach-Object {
    $k, $v = $_ -split '=', 2
    Set-Item -Path "env:$($k.Trim())" -Value $v.Trim()
}
```

En Linux o macOS: `set -a; source .env; set +a`.

**1. Servicio de predicción (Python)**

```powershell
cd prediccion
python -m venv .venv                 # solo la primera vez
.venv\Scripts\Activate.ps1           # Linux/Mac: source .venv/bin/activate
pip install -r requirements.txt      # solo la primera vez
uvicorn app.main:app --port 8000
```

Si PowerShell bloquea la activación: `Set-ExecutionPolicy -Scope CurrentUser RemoteSigned`. Comprueba el servicio con `curl http://localhost:8000/health` (responde `{"estado":"ok"}`). La documentación interactiva está en `http://localhost:8000/docs`.

**2. Backend (Spring Boot)**

```powershell
cd backend
mvn spring-boot:run "-Dspring-boot.run.profiles=supabase"
```

Comprueba con `curl http://localhost:8080/api/health`. En el primer arranque se crea el administrador con `ADMIN_EMAIL` y `ADMIN_PASSWORD`. Después puedes quitar `ADMIN_PASSWORD`.

**3. Aplicación de escritorio (JavaFX)**

```bash
cd desktop
mvn javafx:run
```

Si el backend no está en `http://localhost:8080/api`:

```bash
mvn javafx:run -Dsmartstock.api=https://mi-servidor.example.com/api
```

### D. Docker Compose

```bash
docker compose up --build
```

Levanta el servicio de predicción (puerto 8000) y el backend (puerto 8080, perfil `supabase`, leyendo el `.env`). La aplicación de escritorio se ejecuta fuera de Docker (`cd desktop && mvn javafx:run`). La base de datos es Supabase y no se levanta aquí.

## Roles y permisos

| Acción | Administrador | Encargado de inventario | Usuario |
|---|:---:|:---:|:---:|
| Consultar productos, inventario, alertas, reposiciones y reportes | ✔ | ✔ | ✔ |
| Crear, editar y dar de baja productos, categorías y proveedores | ✔ | ✔ | — |
| Registrar movimientos y editar ubicaciones | ✔ | ✔ | — |
| Resolver alertas | ✔ | ✔ | — |
| Generar, confirmar y rechazar reposiciones | ✔ | ✔ | — |
| Gestionar usuarios y consultar roles | ✔ | — | — |
| Consultar auditoría | ✔ | — | — |

El menú y los botones de la aplicación de escritorio se ajustan al rol.

## Predicción y reposición

Al pulsar **Generar sugerencias** en la pantalla de Reposiciones, el backend envía al servicio Python, para cada producto activo, la existencia, el stock mínimo y el historial de salidas de los últimos 90 días. El servicio devuelve la demanda esperada y la cantidad sugerida. El usuario revisa cada sugerencia y la **confirma** (eligiendo proveedor y cantidad) o la **rechaza**.

Según los días de historial disponibles, se aplica un modelo distinto:

| Historial | Modelo | Qué hace |
|---|---|---|
| Menos de 7 días, o sin ventas | `regla_minimo` | Si la existencia ≤ mínimo, sugiere reponer hasta `2 × mínimo` |
| 7 a 20 días | `media_movil` | Promedio de los últimos 7 días |
| 21 días o más | El mejor de tres | Compara por backtest (error MAE) `media_movil`, `suavizado_exponencial` (alpha 0,3) y `regresion_lineal` (Ridge con tendencia y día de la semana) |

Cantidad sugerida: `max(0, ceil(demanda del horizonte + stock mínimo − existencia))`, con horizonte de 14 días por defecto.

Cada sugerencia guarda el modelo usado, la demanda estimada y una nota con el motivo. Si el servicio de predicción no responde, el backend devuelve `503` con el mensaje "El servicio de predicción no está disponible". Más detalle y ejemplos de la API en [`prediccion/README.md`](prediccion/README.md).

**Limitaciones:** los modelos solo usan las ventas pasadas de cada producto. No consideran promociones, temporadas, vencimientos ni el tiempo de entrega del proveedor.

## Pruebas

| Suite | Comando | Qué cubre |
|---|---|---|
| Backend | `cd backend && mvn test` | Flujo completo contra H2: login, 401, CRUD de productos, código duplicado, salidas inválidas, alertas, y escape de CSV |
| Predicción (núcleo) | `cd prediccion && python -m unittest discover -s tests -t .` | Serie diaria, `regla_minimo`, `media_movil`, elección de modelo, casos límite |
| Predicción (API) | `cd prediccion && pip install -r requirements-dev.txt && pytest` | `/health`, `/predict` y validación de entrada |

Para el plan de pruebas manuales de extremo a extremo (login, roles, movimientos, alertas, reposiciones, auditoría y CSV), consulta [`docs/PRUEBAS.md`](docs/PRUEBAS.md).

## Solución de problemas

| Síntoma | Causa | Solución |
|---|---|---|
| `JWT_SECRET no esta definida o tiene menos de 32 caracteres` | La variable no llegó al backend | Define `JWT_SECRET` en la terminal (ver opción C) o usa `SmartStock.bat`/Docker |
| `Driver org.postgresql.Driver claims to not accept jdbcUrl, ${SUPABASE_DB_URL}` | `SUPABASE_DB_URL` no está definida | Defínela. Verifica con `echo $env:SUPABASE_DB_URL` |
| `NullPointerException` en `JdbcIsolationDelegate` | Hibernate oculta un fallo de conexión | Busca antes en el log `HikariPool-1 - Exception during pool initialization` o activa `-Dlogging.level.com.zaxxer.hikari=DEBUG` |
| `FATAL: Tenant or user not found` | Host del pooler de otra región o usuario incorrecto | Copia el host y el usuario desde Connect → Session pooler |
| `password authentication failed` | Contraseña de base de datos incorrecta | Restablécela en Project Settings → Database |
| `Schema-validation: missing table [...]` | El esquema SQL no se ejecutó o está incompleto | Ejecuta `docs/ddl/smartstock_schema.sql` en el SQL Editor |
| `503` al generar sugerencias | Servicio de predicción apagado o en otro puerto | Inícialo en el puerto 8000 o ajusta `PREDICTION_URL` |
| `Address already in use` | Puerto 8000 u 8080 ocupado | Cierra el otro proceso o cambia de puerto (`--port`, `PORT`) |
| La activación del venv falla en PowerShell | Política de ejecución | `Set-ExecutionPolicy -Scope CurrentUser RemoteSigned` |
| `Could not import module "app.main"` | `uvicorn` ejecutado fuera de `prediccion/` | `cd prediccion` antes de ejecutar |
| `Unresolved compilation problem` en el escritorio | Error de compilación que el IDE dejó pasar | Ejecuta `mvn clean javafx:run` para ver el error real |
| No se puede iniciar sesión tras 5 intentos | Bloqueo temporal de 5 minutos | Espera o reinicia el backend |

## Seguridad

- **No subas `.env`** ni contraseñas a Git. Si una contraseña se expuso, cámbiala en Supabase.
- Cambia la contraseña del administrador inicial después del primer ingreso (`POST /api/auth/cambiar-password` o desde la aplicación).
- Las credenciales de `dev` (`Admin123*` y el secreto de desarrollo) **no deben usarse en producción**.
- Las contraseñas se guardan con BCrypt. La API usa JWT sin sesiones ni cookies.
- RLS está activado en todas las tablas: la clave pública de Supabase no puede leer los datos. El backend se conecta con el usuario de la base de datos.
- Cada acción de escritura queda en la tabla `auditoria`.

## Estructura del repositorio

```
.
├── backend/        # API REST Spring Boot (ver backend/README.md)
├── desktop/        # Aplicación de escritorio JavaFX (ver desktop/README.md)
├── prediccion/     # Servicio Python de predicción (ver prediccion/README.md)
├── docs/
│   ├── ddl/        # smartstock_schema.sql: crea las tablas en Supabase
│   └── PRUEBAS.md  # Plan de pruebas manuales
├── docker-compose.yml
├── SmartStock.bat  # Arranque completo en Windows
├── .env.example    # Plantilla de variables (copiar a .env, no subir)
└── INSTRUCCIONES.md
```

## Equipo

- Omar Santiago Hernández Lamprea
- Luis Santo Padilla Maestre
- Tomás Estrada Martínez

Profesor: Jamilton Fernando Benavides
