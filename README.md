# Configuración de entornos (Java + Docker + CI/CD)

Este documento resume cómo gestionar la configuración y los secretos de una aplicación en distintos entornos: desarrollo local, Eclipse, Docker, docker-compose y CI/CD (GitHub Actions).

---

## 1. Desarrollo local (Java)

Leer la configuración usando `System.getenv()`:

```java
String host = System.getenv("DB_HOST");
String port = System.getenv("DB_PORT");
String name = System.getenv("DB_NAME");
String user = System.getenv("DB_USER");
String password = System.getenv("DB_PASSWORD");

if (user == null || password == null) {
    throw new RuntimeException("Faltan variables de entorno DB_USER / DB_PASSWORD");
}
```

### Definir variables de entorno en la sesión activa

**Windows (PowerShell):**
```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
$env:DB_NAME="mapa_mundi"
$env:DB_USER="mi_usuario"
$env:DB_PASSWORD="mi_password"
```

**Linux / Mac / WSL:**
```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=mapa_mundi
export DB_USER=mi_usuario
export DB_PASSWORD=mi_password
```

> Las variables solo existen en la sesión activa. Para persistirlas, se pueden añadir a `.bashrc`, `.zshrc` o a las variables de entorno del sistema.

### En Eclipse (Run Configuration)

1. Click derecho sobre el proyecto → **Run As → Run Configurations…**
2. Selecciona tu aplicación → pestaña **Environment**
3. Añade las variables:
   ```
   Name: DB_HOST     Value: localhost
   Name: DB_PORT     Value: 3306
   Name: DB_NAME     Value: mapa_mundi
   Name: DB_USER     Value: mi_usuario
   Name: DB_PASSWORD Value: mi_password
   ```
4. Apply → Run

> Esto hace que `System.getenv("DB_HOST")` funcione al ejecutar la app desde Eclipse.

---

## 2. Dockerfile

Nunca incluir secretos directamente. Declarar todas las variables vacías como documentación:

```dockerfile
# Configuración (no sensible)
ENV DB_HOST=""
ENV DB_PORT=""
ENV DB_NAME=""

# Secretos
ENV DB_USER=""
ENV DB_PASSWORD=""
```

> Los valores reales se pasan al contenedor en tiempo de ejecución. La aplicación los lee con `System.getenv()`.

---

## 3. Docker (runtime)

**Opción A — Variables en línea:**
```bash
docker run \
  -e DB_HOST=localhost \
  -e DB_PORT=3306 \
  -e DB_NAME=mapa_mundi \
  -e DB_USER=mi_usuario \
  -e DB_PASSWORD=mi_password \
  -p 8080:8080 mi-imagen
```

**Opción B — Archivo `.env` (recomendado):**

Archivo `.env`:
```
# Configuración (no sensible)
DB_HOST=mysql.vdlp
DB_PORT=3306
DB_NAME=mapa_mundi

# Secretos
DB_USER=mi_usuario
DB_PASSWORD=mi_password
```

```bash
docker run --env-file .env -p 8080:8080 mi-imagen
```

> Añade `.env` a `.gitignore` para que nunca se suba al repositorio.

---

## 4. Docker Compose

Archivo `.env`:
```
# Configuración (no sensible)
DB_HOST=mysql.vdlp
DB_PORT=3306
DB_NAME=mapa_mundi

# Secretos
DB_USER=mi_usuario
DB_PASSWORD=mi_password
DB_ROOT_PASSWORD=root
```

Archivo `docker-compose.yml`:
```yaml
version: "3.8"

services:
  app:
    build: .
    ports:
      - "8080:8080"
    env_file:
      - .env
    depends_on:
      - db

  db:
    image: mysql:latest
    container_name: mysql_vdlp
    hostname: mysql.vdlp
    ports:
      - "3306:3306"
    env_file:
      - .env
    volumes:
      - ./mysql/conf.d:/etc/mysql/conf.d
      - ./mysql/init:/docker-entrypoint-initdb.d
      - mysql_data:/var/lib/mysql
    restart: always

volumes:
  mysql_data:
```

> `depends_on` solo garantiza que el contenedor `db` haya **arrancado**, no que MySQL esté listo para aceptar conexiones. La aplicación puede fallar al inicio si intenta conectarse antes de que MySQL termine de inicializarse. Se recomienda añadir lógica de reintento en la aplicación.

---

## 5. CI/CD — GitHub Actions

Definir en el repositorio (**Settings → Secrets and variables → Actions**):

- **Variables** (no sensibles): `DB_HOST`, `DB_PORT`, `DB_NAME` → en la pestaña *Variables*.
- **Secretos**: `DB_USER`, `DB_PASSWORD` → en la pestaña *Secrets*.

```yaml
name: Build & Deploy

on:
  push:
    branches: [ "main" ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - name: Build Docker image
        run: docker build -t mi-app .

      - name: Run container
        run: |
          docker run \
            -e DB_HOST=${{ vars.DB_HOST }} \
            -e DB_PORT=${{ vars.DB_PORT }} \
            -e DB_NAME=${{ vars.DB_NAME }} \
            -e DB_USER=${{ secrets.DB_USER }} \
            -e DB_PASSWORD=${{ secrets.DB_PASSWORD }} \
            mi-app
```

> Nunca uses `echo ${{ secrets.DB_PASSWORD }}` en logs. GitHub lo enmascara, pero sigue siendo mala práctica.

---

## 6. Buenas prácticas generales

- No subir `.env` al repositorio.
- No hardcodear credenciales en `config.properties` ni en el código.
- No incluir secretos en la imagen Docker.
- Documentar las variables necesarias sin valores reales (README, Dockerfile vacío).
- Separar visualmente configuración y secretos en el `.env`.
- Para producción avanzada: considerar **Secret Manager** (AWS Secrets Manager, HashiCorp Vault, Azure Key Vault).

---

## 7. Variables: configuración vs secretos

| Variable | Ejemplo | ¿Es secreto? |
|---|---|---|
| `DB_HOST` | `mysql.vdlp` | No |
| `DB_PORT` | `3306` | No |
| `DB_NAME` | `mapa_mundi` | No |
| `DB_USER` | `mi_usuario` | Sí |
| `DB_PASSWORD` | `mi_password` | Sí |
| `DB_ROOT_PASSWORD` | `root` | Sí |

---

## 8. Tabla comparativa por entorno

| Entorno | Mecanismo | Dónde se definen | ¿Se sube al repo? |
|---|---|---|---|
| Desarrollo local | `System.getenv()` | Variables de sesión / sistema | No |
| Eclipse | Run Configurations | Pestaña Environment en Run Config | No |
| Dockerfile | `ENV` (vacío) | El propio Dockerfile | Sí (sin valores) |
| Docker runtime | `-e` / `--env-file` | Línea de comandos / `.env` | No |
| Docker Compose | `env_file` | `.env` | No |
| GitHub Actions | `vars.*` / `secrets.*` | Settings del repositorio | No |