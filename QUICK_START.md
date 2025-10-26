# Quick Start Guide

## Prerequisites

- Java 21
- Maven 3.8+
- Docker & Docker Compose
- jq (for JSON parsing in examples)

## 1. Start Infrastructure

```bash
make up
```

This starts:
- PostgreSQL databases for each service
- Redis
- Kafka + Zookeeper
- Keycloak
- Prometheus
- Grafana

Wait ~30 seconds for services to be healthy.

## 2. Verify Services

```bash
# Check Keycloak
curl http://localhost:8090/health

# Check Prometheus
curl http://localhost:9090/-/ready

# Check Grafana
curl http://localhost:3000/api/health
```

## 3. Build Modules

```bash
make build
```

This compiles all modules and runs tests.

## 4. Get OAuth Token

```bash
TOKEN=$(curl -s -X POST http://localhost:8090/realms/osun-his-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=doctor1&password=doctor123&grant_type=password&client_id=his-client" \
  | jq -r '.access_token')

echo "Token: $TOKEN"
```

**Default Users:**
- doctor1 / doctor123 (DOCTOR role)
- admin / admin123 (ADMIN role)
- nurse1 / nurse123 (NURSE role)

## 5. Test Protected Endpoint

```bash
# Without token (should return 401)
curl -v http://localhost:8080/api/emr/patients

# With token (should return 200)
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/emr/patients
```

## 6. View Observability

- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9090
- **Keycloak Admin**: http://localhost:8090 (admin/admin)

## 7. Run Tests

```bash
make test
```

Uses Testcontainers for isolated testing.

## Common Commands

```bash
make up      # Start all services
make down    # Stop all services
make build   # Build modules
make test    # Run tests
make seed    # Seed Keycloak (TODO)
make lint    # Static analysis
make clean   # Clean artifacts
```

## Troubleshooting

### Keycloak not starting
```bash
docker-compose logs keycloak
docker-compose restart keycloak
```

### Port conflicts
Edit `docker-compose.yml` and change port mappings.

### Build failures
```bash
# Clean and rebuild
make clean
make build
```

## Next Steps

1. Implement Keycloak seeding script
2. Add more domain logic to services
3. Create integration tests
4. Setup production deployment

See `PROJECT_SUMMARY.md` for full details.

