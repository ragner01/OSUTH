package ng.osun.his.appointments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ng.osun.his.appointments.domain.VisitQueue;
import ng.osun.his.appointments.repository.QueueRepository;
import ng.osun.his.appointments.service.QueueService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST API for queue management.
 */
@RestController
@RequestMapping("/api/appointments/queue")
@RequiredArgsConstructor
@Slf4j
public class QueueController {

    private final QueueRepository queueRepository;
    private final QueueService queueService;

    /**
     * Get queue for clinic.
     */
    @GetMapping("/clinic/{clinicId}")
    @PreAuthorize("hasRole('NURSE') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<VisitQueue> getQueue(@PathVariable String clinicId) {
        return queueRepository.findByClinicId(clinicId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get queue position for patient.
     */
    @GetMapping("/position")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'NURSE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getQueuePosition(
            @RequestParam String clinicId, 
            @RequestParam String patientId) {
        
        Integer position = queueService.getQueuePosition(clinicId, patientId);
        
        if (position == null) {
            return ResponseEntity.notFound().build();
        }

        // Get estimated wait time
        queueService.updateQueueEstimates(clinicId);

        return ResponseEntity.ok(Map.of(
            "position", position,
            "clinicId", clinicId,
            "patientId", patientId
        ));
    }

    /**
     * Process next patient in queue.
     */
    @PostMapping("/{clinicId}/next")
    @PreAuthorize("hasRole('NURSE') or hasRole('DOCTOR')")
    public ResponseEntity<Void> processNext(@PathVariable String clinicId) {
        queueService.processNext(clinicId);
        return ResponseEntity.ok().build();
    }

    /**
     * Complete patient and update queue statistics.
     */
    @PostMapping("/{clinicId}/complete")
    @PreAuthorize("hasRole('NURSE') or hasRole('DOCTOR')")
    public ResponseEntity<Void> completePatient(
            @PathVariable String clinicId, 
            @RequestParam String patientId) {
        queueService.completePatient(clinicId, patientId);
        return ResponseEntity.ok().build();
    }

    /**
     * Update queue estimates.
     */
    @PostMapping("/{clinicId}/update-estimates")
    @PreAuthorize("hasRole('NURSE') or hasRole('ADMIN')")
    public ResponseEntity<Void> updateEstimates(@PathVariable String clinicId) {
        queueService.updateQueueEstimates(clinicId);
        return ResponseEntity.ok().build();
    }
}

