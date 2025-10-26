# Phase 3 - Lab & Radiology Orders - COMPLETE ✅

## Summary

Phase 3 delivers a comprehensive lab and radiology order management system with critical value detection, Kafka event publishing, and FHIR-inspired APIs for the Osun State Teaching Hospital HIS.

## Deliverables

### 1. Domain Models ✅

**Order Entity**
- Order number, patient ID, encounter ID
- Type: LAB or RAD
- Status workflow: PLACED → IN_PROGRESS → RESULTED → VERIFIED
- Priority: ROUTINE, URGENT, STAT
- Order items with test codes and panels
- Ordering clinician metadata

**Specimen Entity**
- Specimen tracking
- Collection timestamp and collector
- Status: COLLECTED → TRANSPORTED → RECEIVED → IN_PROGRESS
- Container and storage conditions
- Rejection handling

**Result Entity**
- Result values (numeric, text, coded)
- Reference ranges and units
- Abnormal flags (H, L, >, <)
- Critical value detection
- Verification workflow
- Performed metadata

**ImagingStudy Entity**
- Modality (X-RAY, CT, MRI, ULTRASOUND, etc.)
- Body part
- Study metadata
- Report attachments
- Radiologist findings and impression

### 2. Business Logic ✅

**OrderWorkflowService**
- Status transition management
- PLACED → IN_PROGRESS → RESULTED → VERIFIED
- Kafka event publishing for each transition
- Order cancellation with reason
- User and timestamp audit trail

**CriticalValueService**
- Configurable critical ranges
- High/low critical thresholds
- Critical value detection
- Integration with alert service

**CriticalAlertService**
- SMS alerts to clinicians (no PHI)
- In-app detailed alerts
- Notification logging

**ReflexTestingService**
- Automatic follow-up test suggestions
- Rules-based logic
- Examples:
  - High glucose → HbA1c
  - High cholesterol → Lipid panel
  - Abnormal TSH → Free T4
  - Elevated PSA → PSA Free

### 3. Kafka Integration ✅

- Event publishing on status transitions
- Topic: `order.events`
- Event structure:
  ```json
  {
    "orderId": "...",
    "orderNumber": "...",
    "patientId": "...",
    "oldStatus": "...",
    "newStatus": "...",
    "userId": "...",
    "timestamp": "..."
  }
  ```

### 4. REST APIs (FHIR-Inspired) ✅

**OrderController**
```
POST   /api/orders               - Place order
GET    /api/orders/{id}          - Get order
GET    /api/orders/patient/{id}  - Get patient orders
POST   /api/orders/{id}/start    - Start processing
POST   /api/orders/{id}/result  - Mark resulted
POST   /api/orders/{id}/verify  - Verify order
DELETE /api/orders/{id}          - Cancel order
```

**ResultController**
```
POST   /api/orders/results                     - Enter result
GET    /api/orders/results/{id}                - Get result
PUT    /api/orders/results/{id}/verify         - Verify result
GET    /api/orders/results/patient/{id}         - Get patient results
GET    /api/orders/results/patient/{id}/recent  - Get last 100 results
GET    /api/orders/results/critical             - Get critical results
```

**SpecimenController**
```
POST   /api/orders/specimens                - Collect specimen
PUT    /api/orders/specimens/{id}/receive    - Receive specimen
POST   /api/orders/specimens/{id}/reject     - Reject specimen
GET    /api/orders/specimens/order/{orderId} - Get order specimens
```

**ImagingController**
```
POST   /api/orders/imaging              - Register study
PUT    /api/orders/imaging/{id}/report  - Upload report
GET    /api/orders/imaging/{id}         - Get study
GET    /api/orders/imaging/patient/{id} - Get patient studies
```

### 5. Security ✅

- RBAC enforced:
  - **DOCTOR**: Place orders, view results
  - **LAB_SCIENTIST**: Enter results, mark in-progress
  - **RADIOLOGIST**: Upload imaging reports
  - **ADMIN**: Full access
- Method-level `@PreAuthorize` on all endpoints
- Audit trail for all status transitions

### 6. Performance Optimizations ✅

- Indexed queries for fast lookups
- Pagination for result listing
- Batch fetch for last 100 results (p95 < 250ms target)
- Optimized for result timeline queries

### 7. Database Schema ✅

- Complete Flyway migration
- 6 tables: orders, order_items, specimens, results, imaging_studies, imaging_report_attachments
- All indexes for performance
- Foreign key constraints
- Audit fields on all tables

---

## Critical Value Detection

### Examples

| Test | Critical High | Critical Low |
|------|---------------|--------------|
| Glucose | >250 mg/dL | <40 mg/dL |
| Potassium | >6.5 mEq/L | <2.5 mEq/L |
| Sodium | >160 mEq/L | <120 mEq/L |
| Creatinine | >5.0 mg/dL | - |
| Hemoglobin | - | <7.0 g/dL |
| Platelet | - | <50 × 10³/µL |

**Alert Behavior:**
- Immediate SMS to ordering clinician
- In-app detailed alert with full PHI
- Logged to audit trail
- Event published to Kafka

---

## Reflex Testing Rules

Example automatic follow-up suggestions:

1. **High Glucose (≥126 mg/dL)** → Suggest HbA1c
2. **High Cholesterol (≥240 mg/dL)** → Suggest Lipid Panel
3. **Abnormal TSH (<0.5 or >5.0)** → Suggest Free T4
4. **Elevated PSA (≥4.0 ng/mL)** → Suggest PSA Free

---

## Workflow Example

### Lab Order Flow

1. **Doctor places order**
   - POST /api/orders
   - Status: PLACED

2. **Lab scientist starts processing**
   - POST /api/orders/{id}/start
   - Status: IN_PROGRESS
   - Kafka event published

3. **Specimen collected**
   - POST /api/orders/specimens
   - Specimen status: COLLECTED

4. **Results entered**
   - POST /api/orders/results
   - Critical values detected
   - Alert sent if critical

5. **Results verified**
   - PUT /api/orders/results/{id}/verify
   - Order status: VERIFIED
   - Kafka event published

### Radiology Flow

1. **Doctor orders imaging**
   - POST /api/orders (type=RAD)

2. **Radiologist registers study**
   - POST /api/orders/imaging
   - Modality, body part recorded

3. **Study performed**
   - Imaging completed
   - Report attached

4. **Report uploaded**
   - PUT /api/orders/imaging/{id}/report
   - Findings and impression added
   - Status: VERIFIED

---

## Files Created

### Domain
- `domain/Order.java`
- `domain/Specimen.java`
- `domain/Result.java`
- `domain/ImagingStudy.java`

### Repositories
- `repository/OrderRepository.java`
- `repository/SpecimenRepository.java`
- `repository/ResultRepository.java`
- `repository/ImagingStudyRepository.java`

### Services
- `service/OrderWorkflowService.java` (with Kafka)
- `service/CriticalValueService.java`
- `service/CriticalAlertService.java`
- `service/ReflexTestingService.java`

### Database
- `resources/db/migration/V1__Create_orders_tables.sql`

---

## Kafka Events

### Order Status Change Event

**Topic:** `order.events`

**Example:**
```json
{
  "orderId": "order-123",
  "orderNumber": "LAB-2024-001234",
  "patientId": "patient-456",
  "oldStatus": "IN_PROGRESS",
  "newStatus": "RESULTED",
  "userId": "lab-scientist-1",
  "timestamp": "2024-12-15T10:30:00Z"
}
```

### Event Consumers

- **Notification Service**: Send alerts on critical values
- **Analytics Service**: Track processing times
- **External Systems**: HL7 message generation

---

## Testing Recommendations

### Unit Tests
- Critical value detection logic
- Reflex testing rules
- Workflow state transitions

### Integration Tests
- Full workflow: Place → Process → Result → Verify
- Critical value alert triggering
- Kafka event publishing

### Performance Tests
- Last 100 results query (p95 < 250ms)
- Concurrent result entry
- Large batch operations

---

## Usage Example

### Place Lab Order

```bash
curl -X POST http://localhost:8084/api/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "patient-123",
    "encounterId": "encounter-456",
    "type": "LAB",
    "priority": "URGENT",
    "clinicalIndication": "Chest pain",
    "items": [
      {
        "testCode": "CBC",
        "testName": "Complete Blood Count",
        "priority": "URGENT"
      }
    ]
  }'
```

### Enter Result

```bash
curl -X POST http://localhost:8084/api/orders/results \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderItemId": "item-789",
    "patientId": "patient-123",
    "testCode": "GLUCOSE",
    "testName": "Glucose",
    "valueNumeric": 280,
    "units": "mg/dL",
    "referenceRange": "70-100 mg/dL",
    "abnormalFlag": true,
    "abnormalFlagText": "H"
  }'
```

**Result:** Critical value detected → Alert sent to clinician

---

## Status

**Phase 3 Status:** ✅ COMPLETE  
**Production Ready:** Yes (with SMS gateway integration)  
**Next Phase:** Billing & Pharmacy

---

**Date:** December 2024  
**Version:** 1.0.0-SNAPSHOT

