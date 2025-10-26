# ADR 002: PHI Encryption Strategy

## Status
Accepted

## Context
Protected Health Information (PHI) must be encrypted-at-rest to comply with HIPAA-equivalent regulations in Nigeria.

## Decision
Use **AES-256-GCM encryption** with envelope encryption pattern via JPA AttributeConverters.

## Rationale
1. **Field-level encryption** - granular control over sensitive fields
2. **JPA AttributeConverters** - transparent encryption/decryption
3. **Envelope encryption** - master key rotation without re-encrypting data
4. **Performance** - minimal overhead with caching

## Implementation
```java
@Convert(converter = PhiAttributeConverter.class)
private String nin; // Encrypted
```

## Sensitive Fields
- Patient names (first, last, middle)
- Date of birth
- NIN
- Phone number
- Email address
- Address

## Future Enhancement
- Use KMS (AWS KMS, Azure Key Vault) for key management
- Key rotation policies
- Separate encryption keys per tenant/department

## Alternatives Considered
- Database-level encryption (TDE): All-or-nothing approach
- Application-level encryption: Manual handling
- Hash-only: No decryption capability

## Consequences
- Query limitations on encrypted fields (can't index encrypted values)
- Key management overhead
- Performance impact (mitigate with caching)

