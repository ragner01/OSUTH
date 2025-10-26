# ADR 004: PHI Audit Design

## Status
Accepted

## Context
Compliance requires immutable audit logs for all PHI access with tamper-evidence, purpose-of-use logging, and row-level access tracking.

## Decision
Implement **immutable audit trail** with signed hashes, dual storage (DB + Kafka), and purpose-of-use context.

## Rationale
1. **Tamper-evidence** - SHA-256 signed hashes
2. **Dual storage** - DB for queries, Kafka for streaming/backup
3. **Purpose-of-use** - track why data was accessed
4. **Correlation IDs** - trace requests across services
5. **Row-level audit** - who/what/when/why

## Audit Event Structure
```java
{
  "eventTimestamp": "2024-01-15T10:30:00Z",
  "userId": "doctor1",
  "userRole": "DOCTOR",
  "actionType": "READ",
  "resourceType": "Patient",
  "resourceId": "patient-123",
  "purposeOfUse": "TREATMENT",
  "ipAddress": "192.168.1.100",
  "correlationId": "abc-123",
  "signedHash": "sha256(...)"
}
```

## Action Types
- CREATE, READ, UPDATE, DELETE
- SEARCH, EXPORT, LOGIN, LOGOUT

## Implementation
1. **Aspect-oriented auditing** - `@Audit` annotation
2. **Servlet filter** - capture correlation IDs
3. **Kafka producer** - stream audit events
4. **Hash verification** - tamper detection

## Masking in Logs
- NIN: `12345678901` → `123****8901`
- Phone: `+2341234567890` → `+234****7890`
- Email: `john@example.com` → `j***@example.com`

## Alternatives Considered
- Database triggers only: No streaming, complex to verify
- External audit service: Additional dependency
- No audit: Non-compliant

## Consequences
- Storage overhead (mitigate with retention policies)
- Performance impact (asynchronous logging)
- Compliance-ready for HIPAA, GDPR

