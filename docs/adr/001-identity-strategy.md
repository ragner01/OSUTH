# ADR 001: Identity Strategy

## Status
Accepted

## Context
The system requires centralized identity and access management for hospital staff (doctors, nurses, administrative staff, etc.) across multiple microservices.

## Decision
Use **Keycloak** as the identity provider and authorization server with OIDC/OAuth2.

## Rationale
1. **Open source** with enterprise features
2. **Supports SAML, OIDC, OAuth2** - flexibility for future integrations
3. **User federation** - can integrate with AD/LDAP
4. **Fine-grained authorization** - roles, groups, and custom policies
5. **Token validation** - stateless JWT validation at edge (gateway)
6. **Centralized user management** - single source of truth

## Roles
- DOCTOR: View/edit patient records
- NURSE: View patient records, update vitals
- ADMIN: System administration
- CASHIER: Billing and payments
- PHARMACIST: Dispense medications
- LAB_SCIENTIST: Lab orders and results
- RADIOLOGIST: Radiology orders and results
- MATRON: Department oversight
- HMO_OFFICER: Insurance verification

## Implementation
- Gateway validates JWT tokens
- Services extract claims for authorization
- Method-level security with `@PreAuthorize`

## Alternatives Considered
- Spring Authorization Server: Not production-ready for our scale
- Custom JWT implementation: Reinventing the wheel

## Consequences
- All services depend on Keycloak for authentication
- Token validation overhead at gateway
- Single point of failure - mitigate with HA setup

