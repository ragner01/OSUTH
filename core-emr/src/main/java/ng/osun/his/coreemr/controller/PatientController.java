package ng.osun.his.coreemr.controller;

import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ng.osun.his.coreemr.domain.Patient;
import ng.osun.his.coreemr.repository.PatientRepository;
import ng.osun.his.platform.audit.AuditEvent;
import ng.osun.his.platform.audit.AuditService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * REST API for patient management with security, audit, and soft-delete.
 */
@RestController
@RequestMapping("/api/emr/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientRepository patientRepository;
    private final AuditService auditService;

    /**
     * Search patients with pagination and filters.
     */
    @GetMapping
    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE') or hasRole('ADMIN')")
    public ResponseEntity<Page<Patient>> searchPatients(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        
        log.info("Search patients query='{}' by user={}", query, authentication.getName());
        
        Page<Patient> patients;
        if (query != null && !query.isEmpty()) {
            patients = patientRepository.search(query, pageable);
        } else {
            patients = patientRepository.findAllActive(pageable);
        }

        // Audit
        auditService.logEvent(AuditEvent.builder()
            .userId(authentication.getName())
            .userRole(extractRole(authentication))
            .actionType(AuditEvent.ActionType.SEARCH)
            .resourceType("Patient")
            .eventDetails("Searched patients: query=" + query)
            .eventTimestamp(Instant.now())
            .build());

        return ResponseEntity.ok(patients);
    }

    /**
     * Get patient by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE') or hasRole('ADMIN')")
    public ResponseEntity<Patient> getPatient(@PathVariable String id, Authentication authentication) {
        log.info("Get patient id={} by user={}", id, authentication.getName());

        return patientRepository.findById(id)
            .filter(p -> !p.getDeleted())
            .map(patient -> {
                // Audit
                auditService.logEvent(AuditEvent.builder()
                    .userId(authentication.getName())
                    .userRole(extractRole(authentication))
                    .actionType(AuditEvent.ActionType.READ)
                    .resourceType("Patient")
                    .resourceId(id)
                    .eventTimestamp(Instant.now())
                    .build());
                
                return ResponseEntity.ok(patient);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create new patient.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @Transactional
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient, Authentication authentication) {
        log.info("Create patient mrn={} by user={}", patient.getMrn(), authentication.getName());

        // Check for duplicates
        if (patient.getMrn() != null && patientRepository.findByMrn(patient.getMrn()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (patient.getNin() != null && patientRepository.findByNin(patient.getNin()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (patient.getPrimaryPhone() != null && patientRepository.findByPrimaryPhone(patient.getPrimaryPhone()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        patient.setDeleted(false);
        patient.setActive(true);
        Patient saved = patientRepository.save(patient);

        // Audit
        auditService.logEvent(AuditEvent.builder()
            .userId(authentication.getName())
            .userRole(extractRole(authentication))
            .actionType(AuditEvent.ActionType.CREATE)
            .resourceType("Patient")
            .resourceId(saved.getId())
            .eventTimestamp(Instant.now())
            .build());

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Update patient.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @Transactional
    public ResponseEntity<Patient> updatePatient(@PathVariable String id, @RequestBody Patient patient, Authentication authentication) {
        log.info("Update patient id={} by user={}", id, authentication.getName());

        return patientRepository.findById(id)
            .filter(p -> !p.getDeleted())
            .map(existing -> {
                // Update fields
                existing.setFirstName(patient.getFirstName());
                existing.setLastName(patient.getLastName());
                existing.setMiddleName(patient.getMiddleName());
                existing.setDateOfBirth(patient.getDateOfBirth());
                existing.setGender(patient.getGender());
                existing.setTitle(patient.getTitle());
                existing.setPrimaryPhone(patient.getPrimaryPhone());
                existing.setPhones(patient.getPhones());
                existing.setEmail(patient.getEmail());
                existing.setResidentialAddress(patient.getResidentialAddress());
                existing.setAddresses(patient.getAddresses());
                existing.setLga(patient.getLga());
                existing.setState(patient.getState());
                existing.setNationality(patient.getNationality());
                existing.setMaritalStatus(patient.getMaritalStatus());
                existing.setOccupation(patient.getOccupation());
                existing.setBloodGroup(patient.getBloodGroup());
                existing.setGenotype(patient.getGenotype());
                existing.setInsuranceNumber(patient.getInsuranceNumber());
                existing.setInsuranceProvider(patient.getInsuranceProvider());
                existing.setIdentifiers(patient.getIdentifiers());
                existing.setContacts(patient.getContacts());
                existing.setEmergencyContactName(patient.getEmergencyContactName());
                existing.setEmergencyContactPhone(patient.getEmergencyContactPhone());
                existing.setEmergencyContactRelationship(patient.getEmergencyContactRelationship());
                existing.setConsentToTreatment(patient.getConsentToTreatment());
                existing.setConsentToDataSharing(patient.getConsentToDataSharing());
                existing.setActive(patient.getActive());
                
                Patient saved = patientRepository.save(existing);

                // Audit
                auditService.logEvent(AuditEvent.builder()
                    .userId(authentication.getName())
                    .userRole(extractRole(authentication))
                    .actionType(AuditEvent.ActionType.UPDATE)
                    .resourceType("Patient")
                    .resourceId(id)
                    .eventTimestamp(Instant.now())
                    .build());

                return ResponseEntity.ok(saved);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Soft delete patient with reason.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<Void> deletePatient(@PathVariable String id, @RequestParam String reason, Authentication authentication) {
        log.info("Delete patient id={} reason='{}' by user={}", id, reason, authentication.getName());

        if (reason == null || reason.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return patientRepository.findById(id)
            .filter(p -> !p.getDeleted())
            .map(patient -> {
                patient.setDeleted(true);
                patient.setDeletedReason(reason);
                patient.setDeletedAt(Instant.now());
                patientRepository.save(patient);

                // Audit
                auditService.logEvent(AuditEvent.builder()
                    .userId(authentication.getName())
                    .userRole(extractRole(authentication))
                    .actionType(AuditEvent.ActionType.DELETE)
                    .resourceType("Patient")
                    .resourceId(id)
                    .eventDetails("Reason: " + reason)
                    .eventTimestamp(Instant.now())
                    .build());

                return ResponseEntity.ok().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get patient by MRN.
     */
    @GetMapping("/mrn/{mrn}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE') or hasRole('ADMIN')")
    public ResponseEntity<Patient> getPatientByMrn(@PathVariable String mrn, Authentication authentication) {
        return patientRepository.findByMrn(mrn)
            .filter(p -> !p.getDeleted())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    private String extractRole(Authentication authentication) {
        if (authentication.getAuthorities() != null) {
            return authentication.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("UNKNOWN");
        }
        return "UNKNOWN";
    }
}
