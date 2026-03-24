# Document Management Service

API REST en Spring Boot para gestionar documentos PDF (subida, búsqueda y descarga).

## Stack

|      Capa      |            Tecnología             |
|----------------|-----------------------------------|
| Runtime        | Java 17 · Spring Boot 3.4.3       |
| Base de datos  | PostgreSQL 17 *(en RAM, efímero)* |
| Almacenamiento | MinIO (S3-compatible)             |
| API Docs       | Swagger UI (Springdoc OpenAPI 2)  |
| Build          | Maven 3.9.6 (wrapper incluido)    |

---

## Levantar el proyecto

```bash
docker-compose up --build
```

Verifica que la app esté corriendo:

```bash
curl http://localhost:8080/
# → It works!
```

Detener:

```bash
docker-compose down
```

> **⚠️ PostgreSQL corre en RAM (`tmpfs`)** — los datos se borran al detener el stack. El esquema se re-aplica automáticamente en cada arranque desde `docker/init-scripts/schema-init.sql`.

---

## URLs

|      Servicio       |                   URL                   |
|---------------------|-----------------------------------------|
| API                 | <http://localhost:8080>                 |
| **Swagger UI**      | <http://localhost:8080/swagger-ui.html> |
| OpenAPI spec (JSON) | <http://localhost:8080/api-docs>        |
| MinIO Console       | <http://localhost:9001>                 |

---

## Nota sobre OpenAPI vs REQUIREMENTS

El código actual prioriza cumplir la mayoría de los requisitos funcionales (upload, search, download, MinIO + DB) y está alineado con el comportamiento real del controller. Sin embargo, el archivo `docs/document-management-open-api.yml` no documenta por completo ese comportamiento (por ejemplo, **upload multipart/form-data** y el **schema de respuesta** del upload). Se dejó así para no romper el contrato existente, pero el servicio sí implementa la funcionalidad principal descrita en `REQUIREMENTS.md`.

---

## Variables de entorno

Crea un `.env` en la raíz para sobrescribir los defaults:

```dotenv
DB_USERNAME=challenge_user
DB_PASSWORD=challenge_pass
DB_NAME=challenge
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
MINIO_BUCKET=document-bucket
```

---

## Comandos útiles

```bash
# Ejecutar tests dentro de Docker (Java 17)
docker run --rm -v "$PWD":/workspace -w /workspace maven:3.9.6-eclipse-temurin-17 mvn test

# Reporte de cobertura (Jacoco) dentro de Docker

docker run --rm -v "$PWD":/workspace -w /workspace maven:3.9.6-eclipse-temurin-17 mvn verify jacoco:report

./mvnw spotless:apply              # Modifica tus archivos automáticamente para corregir el formato.
./mvnw spotless:check              # Valida el formato. Si encuentra errores, falla el build y te dice qué archivos están mal.
```

## Probar postman

npm i -g newman

```bash
newman run postman/document-management.postman_collection.json
```

---

*Creado por Anibal Gomez*
