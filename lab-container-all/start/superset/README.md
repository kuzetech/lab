# Superset + StarRocks quick start

This directory runs Apache Superset 6.0.0 and StarRocks 4.1.4 using Docker
Compose. On its first start, `starrocks-init` creates and seeds
`gesac_trade.dws_trade_province_order_1d` with 1,000 rows. `superset-init` then
creates the Superset administrator and automatically registers the `StarRocks`
database connection.

## Start

1. Review the administrator credentials and `SUPERSET_SECRET_KEY` in
   `docker-compose.yml`; replace them before non-local use.
2. From this directory, run:

   ```powershell
   docker compose up -d --build
   ```

3. Open Superset at <http://localhost:8088> and sign in with the credentials in
   `docker-compose.yml`. In **Settings → Database Connections**, `StarRocks` is
   ready for SQL Lab and dataset creation; the seeded table is in the
`gesac_trade` database.

The image includes PyMySQL and configures its MySQLdb compatibility hook, so
SQL Lab can query StarRocks through the MySQL-compatible endpoint.

The current `gesac` dashboard is packaged in `assets/gesac-dashboard.zip`.
During every initialization, Superset imports this asset after registering the
StarRocks connection. Therefore, if Superset metadata is rebuilt from scratch,
the dashboard, chart, dataset, and database connection are restored
automatically. To capture later dashboard edits, export it again with:

```powershell
docker compose exec -T superset superset export-dashboards -f /tmp/gesac-dashboard.zip
docker cp superset-starrocks-superset-1:/tmp/gesac-dashboard.zip .\assets\gesac-dashboard.zip
```

Then run `docker compose up -d --build` to package the updated asset.

StarRocks is available to host clients at `localhost:9030` (MySQL protocol) and
its web UI is at <http://localhost:8030>. If another local stack uses these
ports, change the three `ports` mappings directly in `docker-compose.yml`. The
default `root` account has no password and is intentionally only used across
the private Compose network.

## Resources

The local Compose limits are explicit: StarRocks is capped at 3 CPUs / 6 GiB;
Superset at 2 CPUs / 4 GiB. Docker Desktop must therefore have at least 10 GiB
of memory available. Adjust both `cpus`/`mem_limit` and the matching
`deploy.resources.limits` entries together if needed.

## Operations

```powershell
# status and startup logs
docker compose ps
docker compose logs -f

# stop while retaining Superset metadata and StarRocks data
docker compose down
```

To recreate all data intentionally, use `docker compose down -v`.
