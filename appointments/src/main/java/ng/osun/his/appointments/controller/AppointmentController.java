package ng.osun.his.appointments.controller;

// Valid annotation removed for Java 11 compatibility
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ng.osun.his.appointments.domain.Appointment;
import ng.osun.his.appointments.repository.AppointmentRepository;
import ng.osun.his.appointments.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST API for appointment management.
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentService appointmentService;

    /**
     * Book new appointment.
     */
    @PostMapping
    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE') or hasRole('ADMIN') or hasRole('CASHIER')")
    @Transactional
    public ResponseEntity<Appointment> bookAppointment(@RequestBody Appointment appointment, Authentication authentication) {
        try {
            Appointment booked = appointmentService.bookAppointment(appointment, authentication);
            return ResponseEntity.status(201).body(booked);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get appointment by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE') or hasRole('ADMIN')")
    public ResponseEntity<Appointment> getAppointment(@PathVariable String id) {
        return appointmentRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get appointments by patient.
     */
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE') or hasRole('ADMIN')")
    public ResponseEntity<List<Appointment>> getPatientAppointments(@PathVariable String patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patientId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Check-in to appointment.
     */
    @PostMapping("/{id}/check-in")
    @PreAuthorize("hasRole('NURSE') or hasRole('CASHIER') or hasRole('ADMIN')")
    public ResponseEntity<Void> checkIn(@PathVariable String id, Authentication authentication) {
        try {
            appointmentService.checkIn(id, authentication);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Reschedule appointment.
     */
    @PutMapping("/{id}/reschedule")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<Appointment> reschedule(
            @PathVariable String id, 
            @RequestParam LocalDateTime newDateTime,
            Authentication authentication) {
        try {
            Appointment updated = appointmentService.reschedule(id, newDateTime, authentication);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Cancel appointment.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('NURSE')")
    public ResponseEntity<Void> cancel(@PathVariable String id, @RequestParam String reason, Authentication authentication) {
        try {
            appointmentService.cancel(id, reason, authentication);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get appointments by clinic.
     */
    @GetMapping("/clinic/{clinicId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('NURSE') or hasRole('ADMIN')")
    public ResponseEntity<List<Appointment>> getClinicAppointments(
            @PathVariable String clinicId, 
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        List<Appointment> appointments = appointmentRepository.findByClinicIdAndAppointmentDateBetween(clinicId, start, end);
        return ResponseEntity.ok(appointments);
    }
}

