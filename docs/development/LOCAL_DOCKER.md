# Local Docker Development

This document describes the local WCMS stack for Phase 1 development.

## Services

`docker-compose.yml` starts:

- `mariadb`: MariaDB 11.4 on `localhost:3306`
- `redis`: Redis 7.4 on `localhost:6379`
- `rabbitmq`: RabbitMQ 4.0 on `localhost:5672`, management UI on `localhost:15672`
- `auth-service`: Spring Boot auth service inside Docker on container port `8080`
- `nginx`: public entrypoint on `http://localhost`

The host-facing HTTP port is `80`. Do not use `18081` for Docker-based local access.

## Start

From the repository root:

```powershell
docker compose up -d
```

Check status:

```powershell
docker compose ps
curl.exe -i http://localhost/health
curl.exe -i http://localhost/actuator/health
```

Expected health response from auth-service:

```json
{"status":"UP"}
```

## Local Auth Account

The local seed file is `infra/mariadb/init/003-auth-seed.sql`.

Local-only account:

```text
username: platform-admin
password: Password123!
role: SUPER_ADMIN
email: platform-admin@wcms.local
```

This account is for local development only. Do not reuse this password in deployed environments.

## Auth API Smoke Test

Login:

```powershell
$loginBody = @{
  username = 'platform-admin'
  password = 'Password123!'
} | ConvertTo-Json -Compress

$login = Invoke-RestMethod `
  -Uri http://localhost/api/auth/login `
  -Method Post `
  -ContentType 'application/json' `
  -Body $loginBody

$login
```

Refresh:

```powershell
$refreshBody = @{
  refreshToken = $login.refreshToken
} | ConvertTo-Json -Compress

$refreshed = Invoke-RestMethod `
  -Uri http://localhost/api/auth/refresh `
  -Method Post `
  -ContentType 'application/json' `
  -Body $refreshBody

$refreshed
```

Logout:

```powershell
$logoutBody = @{
  refreshToken = $refreshed.refreshToken
} | ConvertTo-Json -Compress

Invoke-WebRequest `
  -UseBasicParsing `
  -Uri http://localhost/api/auth/logout `
  -Method Post `
  -ContentType 'application/json' `
  -Body $logoutBody
```

Expected logout status is `204 No Content`.

## Database Initialization Note

MariaDB init scripts in `infra/mariadb/init` run only when the Docker volume is created for the first time.

If a new init SQL file is added after the volume already exists, either apply the SQL manually or recreate the local volume. Recreating the volume deletes local database data.

Apply a seed SQL manually:

```powershell
Get-Content -Path infra/mariadb/init/003-auth-seed.sql -Raw |
  docker exec -i wcms-mariadb mariadb -uroot -pwcms_root_password
```

Recreate the local database volume only when local data can be discarded:

```powershell
docker compose down -v
docker compose up -d
```

## Backend Test Commands

Run auth-service tests:

```powershell
cd backend/wcms
.\gradlew.bat clean :services:auth-service:test
```

Build the auth-service jar:

```powershell
cd backend/wcms
.\gradlew.bat :services:auth-service:bootJar
```
