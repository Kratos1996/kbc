# Infrastructure

Docker Compose for local development.

## Start MySQL + Adminer

```bash
docker compose up -d
```

- MySQL: `localhost:3306` (user `kbc` / pass `kbc_secret`, db `kbc`)
- Adminer UI: http://localhost:8080  (login with server `mysql`, user `kbc`, pass `kbc_secret`, db `kbc`)

## Stop

```bash
docker compose down              # keep data
docker compose down -v           # delete data
```

## Notes
- First run seeds `utf8mb4` and IST timezone via `./mysql/init/01-init.sql`.
- Production deploy: provision a managed MySQL 8 instance and set `DATABASE_URL` accordingly.
