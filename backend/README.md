# SmartStock ERP — Backend

API REST en **Java 17 + Spring Boot 3.3**. Guarda los datos en PostgreSQL (Supabase),
protege los endpoints con JWT y pide las recomendaciones de reposición al servicio de predicción.

## Perfiles

| Perfil | Base de datos | Uso |
|---|---|---|
| `dev` (por defecto) | H2 en memoria con datos de ejemplo | Probar rápido. Admin: `admin@smartstock.com` / `Admin123*` |
| `supabase` | PostgreSQL en Supabase | Producción |

## Conectar con Supabase (paso a paso)

1. En [supabase.com](https://supabase.com) crea un proyecto y guarda la contraseña de la base.
2. **SQL Editor → New query**: pega y ejecuta `docs/ddl/smartstock_schema.sql`.
   Crea las tablas, los 3 roles y activa RLS (la clave pública de Supabase no podrá leer tus datos).
3. **Project Settings → Database → Connection string**. Elige:
   - *Session pooler* (puerto 5432): funciona en cualquier red con IPv4 (recomendado).
   - *Direct connection*: solo si tu red tiene IPv6.
4. Arma las variables (ver `.env.example`):
   ```
   SPRING_PROFILES_ACTIVE=supabase
   SUPABASE_DB_URL=jdbc:postgresql://aws-0-REGION.pooler.supabase.com:5432/postgres?sslmode=require
   SUPABASE_DB_USER=postgres.PROJECT-REF
   SUPABASE_DB_PASSWORD=la-contraseña-de-tu-base
   JWT_SECRET=<openssl rand -base64 48>
   ADMIN_EMAIL=admin@tudrogueria.com
   ADMIN_PASSWORD=<mínimo 8 caracteres>
   ```
   No uses el puerto 6543 (transaction pooler) con este proyecto.
5. `mvn spring-boot:run` (o `docker compose up`). En el primer arranque se crea el administrador
   con `ADMIN_EMAIL` / `ADMIN_PASSWORD`. Después puedes quitar `ADMIN_PASSWORD`.

El backend arranca con `ddl-auto=validate`: si el esquema no coincide con las entidades, falla con un mensaje claro en vez de modificar la base.

## Endpoints principales (todos bajo `/api`, con `Authorization: Bearer <token>`)

| Recurso | Rutas | Quién escribe |
|---|---|---|
| Auth | `POST /auth/login`, `GET /auth/me`, `POST /auth/cambiar-password` | todos |
| Productos | `GET/POST /productos`, `GET/PUT/DELETE /productos/{id}`, `PATCH /productos/{id}/reactivar` | admin, encargado |
| Categorías / Proveedores | `GET/POST /categorias`, `PUT/DELETE /categorias/{id}` (igual `/proveedores`) | admin, encargado |
| Inventario | `GET /inventario?soloBajoMinimo=`, `PUT /inventario/{idProducto}/ubicacion` | admin, encargado |
| Movimientos | `GET /movimientos`, `POST /movimientos` | admin, encargado |
| Alertas | `GET /alertas?estado=pendiente`, `PUT /alertas/{id}/resolver` | admin, encargado |
| Reposiciones | `GET /reposiciones`, `POST /reposiciones/generar`, `PUT /reposiciones/{id}/confirmar`, `/rechazar` | admin, encargado |
| Reportes | `GET /reportes/dashboard`, `/reportes/inventario/csv`, `/reportes/movimientos/csv` | todos |
| Usuarios, roles, auditoría | `/usuarios`, `/roles`, `/auditoria` | solo admin |

Los `DELETE` son bajas lógicas (el registro queda inactivo) para conservar el historial.
Las consultas pueden hacerlas todos los roles; el rol `usuario` es de solo lectura.

## Reglas de negocio

- Movimientos: *entrada* suma, *salida* resta, *ajuste* fija la existencia (exige motivo). Nunca queda stock negativo.
- Cada movimiento bloquea la fila de inventario (`SELECT ... FOR UPDATE`) para evitar condiciones de carrera.
- Alerta de stock mínimo cuando existencia ≤ mínimo; se resuelve sola al recuperarse.
- Reposición: `POST /reposiciones/generar` envía al servicio Python el historial de salidas (90 días), la existencia y el mínimo de cada producto activo. Si el servicio no responde, devuelve `503` con un mensaje claro.
- Cada acción de escritura queda en la tabla `auditoria`.

## Pruebas

```bash
mvn test        # incluye un flujo completo contra H2 (login, CRUD, movimientos, alertas)
mvn package     # genera target/smartstock-backend.jar
```

## Organización

```
src/main/java/co/edu/umb/smartstock/
├── config/      # Seguridad, JWT, datos iniciales
├── controller/  # Endpoints REST
├── dto/         # Objetos de entrada/salida de la API
├── exception/   # Errores con formato uniforme
├── model/       # Entidades JPA
├── repository/  # Spring Data JPA
└── service/     # Lógica de negocio
```
