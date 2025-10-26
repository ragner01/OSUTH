package ng.osun.his.appointments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ng.osun.his.appointments.domain.Appointment;
import ng.osun.his.appointments.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Notification service for appointment reminders via SMS/WhatsApp.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final AppointmentRepository appointmentRepository;
    
    // TODO: Integrate with SMS/WhatsApp gateway (e.g., Twilio, Vonage)

    /**
     * Send SMS reminder for appointments.
     */
    public void sendReminder(Appointment appointment) {
        // TODO: Implement SMS sending
        // For now, just log the reminder
        
        String message = String.format(
            "Reminder: Your appointment at Osun State Teaching Hospital is on %s. " +
            "Call %s to reschedule.",
            appointment.getAppointmentDate(),
            "0800-OSUTH" // Placeholder
        );

        log.info("SMS reminder sent to patient {} for appointment {}", 
            appointment.getPatientId(), appointment.getAppointmentNumber());

        appointment.setSmsReminderSent(true);
        appointment.setReminderSentAt(Instant.now());
        appointmentRepository.save(appointment);
    }

    /**
     * Send WhatsApp reminder with reschedule link.
     */
    public void sendWhatsAppReminder(Appointment appointment, String rescheduleToken) {
        // TODO: Implement WhatsApp sending with tokenized link
        
        String message = String.format(
            "Reminder: Your appointment at Osun State Teaching Hospital is on %s. " +
            "Reschedule: https://osuth.gov.ng/reschedule?token=%s",
            appointment.getAppointmentDate(),
            rescheduleToken
        );

        log.info("WhatsApp reminder sent to patient {} for appointment {} with token: {}", 
            appointment.getPatientId(), appointment.getAppointmentNumber(), rescheduleToken);
    }

    /**
     * Find appointments needing reminders (24 hours before).
     */
    public List<Appointment> findAppointmentsNeedingReminders() {
        // TODO: Implement proper date range query
        // For now, get all scheduled appointments
        List<Appointment> all = appointmentRepository.findAll();
        return all.stream()
            .filter(apt -> !Boolean.TRUE.equals(apt.getSmsReminderSent()))
            .filter(apt -> "SCHEDULED".equals(apt.getStatus()) || "CONFIRMED".equals(apt.getStatus()))
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Generate reschedule token for patient.
     */
    public String generateRescheduleToken(Appointment appointment) {
        // TODO: Implement proper JWT token with expiration
        return "RSCH-" + appointment.getId() + "-" + System.currentTimeMillis();
    }
}

