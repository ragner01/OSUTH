# ADR 003: Eventing Strategy

## Status
Accepted

## Context
Microservices need to communicate asynchronously for real-time updates, audit logging, and eventual consistency.

## Decision
Use **Apache Kafka** as event streaming platform with event sourcing for audit trail.

## Rationale
1. **Event sourcing** - complete audit history
2. **Async communication** - decoupled services
3. **At-least-once delivery** - reliability
4. **Scalability** - handle high throughput
5. **Replay capability** - debug and recover from failures

## Topics
- `audit.events` - Audit trail (immutable)
- `patient.created` - Patient registration events
- `appointment.scheduled` - Appointment lifecycle
- `order.placed` - Lab/radiology orders
- `notification.required` - Push notifications

## Implementation
- Use Spring Kafka for producers/consumers
- Implement idempotent handlers
- Dead letter queues for failed messages
- Event versioning for schema evolution

## Audit Trail
All PHI access logged to:
1. Database (`audit_events` table)
2. Kafka (`audit.events` topic)
3. Signed hashes for tamper-evidence

## Alternatives Considered
- RabbitMQ: Limited scalability
- Database replication: Not async
- HTTP webhooks: Tight coupling

## Consequences
- Eventual consistency across services
- Message ordering not guaranteed (use partitions)
- Storage overhead for audit logs

