package ng.osun.his.interop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ng.osun.his.coreemr.domain.Patient;
import ng.osun.his.coreemr.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * FHIR-style Patient read API.
 */
@RestController
@RequestMapping("/fhir/Patient")
@RequiredArgsConstructor
@Slf4j
public class FhirPatientController {

    private final PatientRepository patientRepository;

    /**
     * Read patient by ID with ETag support.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> readPatient(@PathVariable String id,
                                         @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch,
                                         Authentication auth) {
        log.info("FHIR Patient read: id={} by user={}", id, auth.getName());

        return patientRepository.findById(id)
            .filter(p -> !p.getDeleted())
            .map(patient -> {
                // Generate ETag from version
                String etag = "\"" + patient.getVersion() + "\"";

                // Check If-None-Match
                if (ifNoneMatch != null && ifNoneMatch.equals(etag)) {
                    return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                        .header(HttpHeaders.ETAG, etag)
                        .build();
                }

                return ResponseEntity.ok()
                    .header(HttpHeaders.ETAG, etag)
                    .body(mapToFhirPatient(patient));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Search patients with FHIR search parameters.
     */
    @GetMapping
    public ResponseEntity<?> searchPatients(
            @RequestParam(required = false) String identifier,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String birthdate,
            @RequestParam(required = false) String gender,
            Pageable pageable,
            Authentication auth) {
        
        log.info("FHIR Patient search by user={}", auth.getName());

        Page<Patient> patients;
        
        if (identifier != null) {
            // Search by MRN or NIN
            patients = patientRepository.findByMrnContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrNinContainingIgnoreCaseOrPrimaryPhoneContainingIgnoreCase(
                identifier, identifier, identifier, identifier, identifier, pageable);
        } else if (name != null) {
            patients = patientRepository.search(name, pageable);
        } else {
            patients = patientRepository.findAllActive(pageable);
        }

        // Map to FHIR Bundle
        return ResponseEntity.ok()
            .header("X-Total-Count", String.valueOf(patients.getTotalElements()))
            .body(mapToFhirBundle(patients));
    }

    private Object mapToFhirPatient(Patient patient) {
        return java.util.Map.of(
            "resourceType", "Patient",
            "id", patient.getId(),
            "identifier", java.util.List.of(
                java.util.Map.of("system", "http://osun.his.ng/mrn", "value", patient.getMrn()),
                patient.getNin() != null ? java.util.Map.of("system", "http://osun.his.ng/nin", "value", patient.getNin()) : null
            ).stream().filter(java.util.Objects::nonNull).collect(java.util.stream.Collectors.toList()),
            "name", java.util.List.of(
                java.util.Map.of(
                    "family", patient.getLastName(),
                    "given", java.util.List.of(patient.getFirstName())
                )
            ),
            "gender", patient.getGender(),
            "birthDate", patient.getDateOfBirth() != null ? patient.getDateOfBirth().toString() : null,
            "telecom", patient.getPrimaryPhone() != null ? 
                java.util.List.of(java.util.Map.of("system", "phone", "value", patient.getPrimaryPhone())) : 
                java.util.List.of()
        );
    }

    private Object mapToFhirBundle(Page<Patient> patients) {
        return java.util.Map.of(
            "resourceType", "Bundle",
            "type", "searchset",
            "total", patients.getTotalElements(),
            "entry", patients.getContent().stream()
                .map(p -> java.util.Map.of("resource", mapToFhirPatient(p)))
                .collect(java.util.stream.Collectors.toList())
        );
    }
}

