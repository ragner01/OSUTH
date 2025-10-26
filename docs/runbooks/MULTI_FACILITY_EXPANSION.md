# Multi-Facility Expansion Guide

## Overview

This guide describes how to expand Osun HIS to support multiple healthcare facilities while maintaining data isolation and enabling cross-facility referrals.

---

## 🏥 Facility Types

### Primary (PHC)
- Basic health services
- Immunization
- Outpatient care

### Secondary (General Hospital)
- Emergency services
- Inpatient care
- Basic surgeries

### Tertiary (Teaching Hospital)
- Specialized care
- Advanced surgeries
- Teaching & research

### Specialist
- Cardiology, Oncology, etc.
- Specialized services

---

## 📋 Adding a New Facility

### 1. Register Facility in System

```sql
INSERT INTO facilities (id, name, lga, facility_type, active, created_at)
VALUES (
    'FAC003',
    'Iwo General Hospital',
    'Iwo',
    'SECONDARY',
    true,
    CURRENT_TIMESTAMP
);
```

### 2. Configure Facility Context

```java
@Configuration
public class FacilityConfig {
    
    @Bean
    public FacilityRegistry facilityRegistry() {
        return FacilityRegistry.builder()
            .facility(Facility.builder()
                .id("FAC003")
                .name("Iwo General Hospital")
                .lga("Iwo")
                .type("SECONDARY")
                .active(true)
                .build())
            .build();
    }
}
```

### 3. Deploy with Facility Isolation

```bash
# Deploy with facility-specific configuration
env FACILITY_ID=FAC003 ./deploy.sh

# Verify facility context
curl http://api.osun-his.ng/api/admin/facilities/FAC003/verify
```

### 4. Data Migration (If Needed)

```bash
# Migrate existing data to new facility
./scripts/migrate-to-facility.sh --source FAC001 --target FAC003 --dry-run

# Execute migration
./scripts/migrate-to-facility.sh --source FAC001 --target FAC003
```

---

## 🔐 Data Isolation

### Row-Level Security (RLS)

All queries automatically filter by facilityId:

```java
public interface PatientRepository extends JpaRepository<Patient, String> {
    
    @Query("SELECT p FROM Patient p WHERE p.facilityId = ?#{@facilityContext.getCurrentFacilityId()}")
    Page<Patient> findAllForCurrentFacility(Pageable pageable);
    
    // Facility context automatically applied
    Optional<Patient> findByMrnAndFacilityId(String mrn, String facilityId);
}
```

### Security Enforcement

```java
@PreAuthorize("hasFacilityAccess(#facilityId)")
public Patient createPatient(String facilityId, PatientDTO dto) {
    // User must have access to this facility
    return patientService.createPatient(facilityId, dto);
}
```

---

## 🔄 Cross-Facility Referrals

### Outgoing Referral

```java
@Service
public class ReferralService {
    
    public Referral createReferral(String fromFacilityId, 
                                   String toFacilityId,
                                   String patientId,
                                   String reason) {
        
        // Verify both facilities exist
        facilityRegistry.verifyFacility(toFacilityId);
        
        // Create referral
        Referral referral = new Referral();
        referral.setFromFacilityId(fromFacilityId);
        referral.setToFacilityId(toFacilityId);
        referral.setPatientId(patientId);
        referral.setReason(reason);
        referral.setStatus("PENDING");
        
        return referralRepository.save(referral);
    }
}
```

### Incoming Referral Acceptance

```java
public void acceptReferral(String referralId, String acceptingFacilityId) {
    Referral referral = referralRepository.findById(referralId)
        .orElseThrow();
    
    // Verify referral is for this facility
    if (!referral.getToFacilityId().equals(acceptingFacilityId)) {
        throw new UnauthorizedException();
    }
    
    referral.setStatus("ACCEPTED");
    referral.setAcceptedAt(Instant.now());
    
    referralRepository.save(referral);
}
```

---

## 📊 Facility-Level Analytics

### Dashboard per Facility

```sql
-- Patients per facility
SELECT facility_id, COUNT(*) as patient_count
FROM patients
WHERE active = true AND deleted = false
GROUP BY facility_id;

-- Appointments per facility
SELECT f.facility_id, COUNT(a.id) as appointment_count
FROM facilities f
LEFT JOIN appointments a ON a.facility_id = f.id
WHERE f.active = true
GROUP BY f.facility_id;
```

### Cross-Facility Reports

```sql
-- Referral statistics
SELECT 
    from_facility_id,
    to_facility_id,
    COUNT(*) as referral_count
FROM referrals
WHERE created_at >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY from_facility_id, to_facility_id
ORDER BY referral_count DESC;
```

---

## 🚀 Deployment Strategy

### Rolling Deployment

Deploy one facility at a time:

```bash
# Week 1: Deploy to Osogbo Teaching Hospital
env FACILITY_ID=FAC001 ./deploy-to-facility.sh

# Week 2: Deploy to Ejigbo PHC
env FACILITY_ID=FAC002 ./deploy-to-facility.sh

# Week 3: Deploy to Iwo General Hospital
env FACILITY_ID=FAC003 ./deploy-to-facility.sh
```

### Canary Deployment per Facility

```bash
# Deploy new version to 10% of users in FAC001
env FACILITY_ID=FAC001 TRAFFIC_SPLIT=10 ./canary-deploy.sh

# Monitor for 1 hour

# Increase to 50%
env FACILITY_ID=FAC001 TRAFFIC_SPLIT=50 ./canary-deploy.sh

# Full deployment
env FACILITY_ID=FAC001 ./full-deploy.sh
```

---

## 📋 Facility Rollout Checklist

- [ ] Facility registered in system
- [ ] Staff accounts created
- [ ] Data migration completed (if any)
- [ ] Network connectivity verified
- [ ] Hardware inventory received
- [ ] User training completed
- [ ] Go-live date confirmed
- [ ] Support team briefed
- [ ] Emergency contacts updated

---

## 🔍 Monitoring per Facility

### Metrics to Track

1. **Patient Registration**
   - Daily patient count per facility
   - Growth trend analysis

2. **Clinical Activity**
   - Appointments per facility
   - Lab tests per facility
   - Pharmacy dispenses per facility

3. **Financial Performance**
   - Revenue per facility
   - Payer mix per facility
   - AR aging by facility

### Grafana Dashboard

Create facility-specific dashboards:

```json
{
  "dashboard": {
    "title": "Facility: Iwo General Hospital",
    "variables": [
      {
        "name": "facility_id",
        "value": "FAC003"
      }
    ]
  }
}
```

---

## 🏗️ Infrastructure Scaling

### Additional Facilities

As you add facilities, consider:

1. **Database Sharding**
   - Partition by facilityId
   - Separate databases for large facilities

2. **Load Balancing**
   - Route by facilityId
   - Geographic distribution

3. **Cache Strategy**
   - Per-facility Redis instances
   - Cache warming per facility

---

## 📞 Support Contacts

- **Facility Administrator:** [INSERT]
- **Technical Support:** [INSERT]
- **Hospital IT Director:** [INSERT]

---

**Last Updated:** [DATE]  
**Next Review:** [DATE + 6 months]

