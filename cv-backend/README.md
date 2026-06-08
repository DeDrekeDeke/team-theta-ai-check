# CV Backend

Spring Boot backend for the educational CV Manager starter application.

## Run Locally

Start PostgreSQL from the repository root:

```bash
docker compose up -d db
```

Then run the backend:

```bash
cd cv-backend
./gradlew bootRun
```

On Windows PowerShell:

```powershell
cd cv-backend
.\gradlew.bat bootRun
```

The project targets Java 21 and includes a project-local Gradle wrapper.

The backend runs at:

```text
http://localhost:8080
```

Health check:

```text
GET http://localhost:8080/api/health
```

## API

Demo login:

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "alice@example.com",
  "password": "user123"
}
```

CV endpoints:

```text
GET  /api/cvs
GET  /api/cvs/{id}
GET  /api/cvs/{id}/html
GET  /api/cvs/search?q=alice
POST /api/cvs/upload
PUT  /api/cvs/{id}
POST /api/cvs/legacy-preview
```

Upload expects `multipart/form-data` fields:

```text
ownerUserId
title
file
```

Admin/settings endpoints:

```text
GET /api/admin/settings
PUT /api/admin/settings/{key}
GET /api/users
GET /api/users/{id}
```

AI placeholder endpoints:

```text
GET  /api/cvs/{cvId}/ai-actions/suggestions
POST /api/cvs/{cvId}/ai-actions/improve-summary
```

Default local database settings:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/cvmanager
SPRING_DATASOURCE_USERNAME=cvmanager
SPRING_DATASOURCE_PASSWORD=cvmanager
```
