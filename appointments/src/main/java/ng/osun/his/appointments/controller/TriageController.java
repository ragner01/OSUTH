package ng.osun.his.appointments.controller;

// Valid annotation removed
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ng.osun.his.appointments.domain.TriageScore;
import ng.osun.his.appointments.repository.TriageScoreRepository;
import ng.osun.his.appointments.service.TriageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST API for triage scoring.
 */
@RestController
@RequestMapping("/api/appointments/triage")
@RequiredArgsConstructor
@Slf4j
public class TriageController {

    private final TriageScoreRepository triageScoreRepository;
    private final TriageService triageService;

    /**
     * Create triage score for appointment.
     */
    @PostMapping
    @PreAuthorize("hasRole('NURSE') or hasRole('DOCTOR')")
    public ResponseEntity<TriageScore> createTriage(@RequestBody TriageScore triage) {
        // Calculate score
        TriageScore calculated = triageService.calculateTriageScore(triage);
        
        // Get queue priority
        int queuePriority = triageService.getQueuePriority(calculated.getPriorityLevel());
        
        log.info("Triage score calculated: {} for appointment: {} with priority level: {} and queue priority: {}",
            calculated.getCalculatedScore(), calculated.getAppointmentId(), 
            calculated.getPriorityLevel(), queuePriority);
        
        return ResponseEntity.ok(triageScoreRepository.save(calculated));
    }

    /**
     * Get triage score by appointment ID.
     */
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasRole('NURSE') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<TriageScore> getTriageByAppointment(@PathVariable String appointmentId) {
        return triageScoreRepository.findByAppointmentId(appointmentId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}

