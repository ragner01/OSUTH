# Osun State Teaching Hospital HIS

## Project Overview

Production-grade multi-module monorepo for Hospital Information System serving Osun State Teaching Hospital, Nigeria.

**Technology Stack:**
- Java 21
- Spring Boot 3.3+
- Maven (Multi-module)
- PostgreSQL 16
- Redis
- Kafka
- Keycloak (Identity & Access Management)
- Prometheus + Grafana (Observability)

## Architecture

### Modules

- **platform-lib**: Shared utilities, PHI encryption, audit trail, correlation IDs
- **identity**: Keycloak integration and identity management
- **gateway**: Spring Cloud Gateway with security and routing
- **core-emr**: Electronic Medical Records with PHI handling
- **appointments**: Patient appointment scheduling
- **orders-lab-rad**: Laboratory and radiology orders
- **pharmacy**: Pharmacy and medication management
- **billing**: Billing and payment management
- **inventory**: Inventory management
- **staff-rota**: Staff scheduling and rota
- **notifications**: Push notifications and messaging
- **ops-analytics**: Operational analytics and reporting
- **interop**: HL7 FHIR and external system integration

### Security & Compliance

- **OIDC via Keycloak**: Centralized authentication and authorization
- **Roles**: DOCTOR, NURSE, ADMIN, CASHIER, PHARMACIST, LAB_SCIENTIST, RADIOLOGIST, MATRON, HMO_OFFICER
- **PHI Encryption**: Encryption-at-rest using AES-256-GCM with JPA AttributeConverters
- **Audit Trail**: Immutable DB + Kafka audit events with signed hashes for tamper-evidence
- **Row-level Access Control**: Purpose-of-use logging, consent flags

### Nigeria Context

- **Currency**: NGN
- **Timezone**: Africa/Lagos
- **Locale**: en-NG
- **Phone Validation**: +234 format
- **NIN Support**: National Identification Number (11 digits)
- **LGAs**: Osun State Local Government Areas seed data

## Getting Started

### Prerequisites

- Java 21
- Maven 3.8+
- Docker & Docker Compose

### Quick Start

```bash
# Start infrastructure services
make up

# Build all modules
make build

# Seed Keycloak
make seed

# Run tests
make test
```

### Service Ports

| Service | Port |
|---------|------|
| Gateway | 8080 |
| Identity | 8081 |
| Core EMR | 8082 |
| Appointments | 8083 |
| Orders | 8084 |
| Pharmacy | 8085 |
| Billing | 8086 |
| Inventory | 8087 |
| Staff Rota | 8088 |
| Notifications | 8089 |
| Keycloak | 8090 |
| Ops Analytics | 8091 |
| Interop | 8092 |
| Prometheus | 9090 |
| Grafana | 3000 |
| Redis | 6379 |
| Kafka | 9092 |

## Development

### Testing Protected Endpoints

1. **Get OAuth token from Keycloak:**
```bash
curl -X POST http://localhost:8090/realms/osun-his-realm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=doctor1&password=password&grant_type=password&client_id=his-client"
```

2. **Call protected API:**
```bash
curl -H "Authorization: Bearer <TOKEN>" \
  http://localhost:8080/api/emr/patients
```

3. **Expected response:**
- 401 without token
- 200 with valid token and DOCTOR role
- Audit event logged to database and Kafka

## Observability

- **Metrics**: Micrometer → Prometheus (http://localhost:9090)
- **Dashboards**: Grafana (http://localhost:3000)
- **Tracing**: OpenTelemetry spans across services
- **Logging**: Structured logging with correlation IDs

## Documentation

See [docs/adr/](./adr/) for Architecture Decision Records:
- Identity Strategy
- Encryption Strategy
- Eventing Strategy
- PHI Audit Design

## License

Proprietary - Osun State Teaching Hospital

