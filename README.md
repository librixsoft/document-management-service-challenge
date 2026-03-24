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
./mvnw test                        # Ejecutar tests
./mvnw spotless:apply              # Formatear código
./mvnw verify jacoco:report        # Reporte de cobertura
```

---

*Creado por Anibal Gomez*
