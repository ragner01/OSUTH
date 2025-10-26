package ng.osun.his.appointments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ng.osun.his.appointments.domain.Appointment;
import ng.osun.his.appointments.domain.Clinic;
import ng.osun.his.appointments.repository.AppointmentRepository;
import ng.osun.his.appointments.repository.ClinicRepository;
import ng.osun.his.appointments.repository.ProviderRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for appointment booking with conflict prevention and overbooking logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ClinicRepository clinicRepository;
    private final ProviderRepository providerRepository;

    /**
     * Book appointment with double-booking and overlap prevention.
     */
    @Transactional
    public Appointment bookAppointment(Appointment appointment, Authentication authentication) {
        // Validate clinic exists and is active
        Clinic clinic = clinicRepository.findById(appointment.getClinicId())
            .orElseThrow(() -> new IllegalStateException("Clinic not found"));

        if (!clinic.getActive()) {
            throw new IllegalStateException("Clinic is inactive");
        }

        // Check for blackout dates
        if (clinic.getBlackoutDates() != null && clinic.getBlackoutDates().contains(appointment.getAppointmentDate().toLocalDate())) {
            throw new IllegalStateException("Clinic is closed on this date");
        }

        // Validate provider exists if specified
        if (appointment.getProviderId() != null) {
            providerRepository.findById(appointment.getProviderId())
                .ifPresentOrElse(
                    provider -> {
                        if (!provider.getActive()) {
                            throw new IllegalStateException("Provider is inactive");
                        }
                    },
                    () -> {
                        throw new IllegalStateException("Provider not found");
                    }
                );
        }

        // Check for conflicts with existing appointments
        checkForConflicts(appointment);

        // Generate appointment number
        appointment.setAppointmentNumber(generateAppointmentNumber(appointment));

        // Set end time
        if (appointment.getDurationMinutes() == null) {
            appointment.setDurationMinutes(clinic.getSlotDurationMinutes());
        }
        appointment.setAppointmentEndDate(
            appointment.getAppointmentDate().plusMinutes(appointment.getDurationMinutes()));

        // Set status
        appointment.setStatus("SCHEDULED");

        log.info("Booking appointment: {} for patient: {} in clinic: {}", 
            appointment.getAppointmentNumber(), appointment.getPatientId(), appointment.getClinicId());

        return appointmentRepository.save(appointment);
    }

    /**
     * Check for conflicts with existing appointments.
     */
    private void checkForConflicts(Appointment appointment) {
        LocalDateTime start = appointment.getAppointmentDate();
        LocalDateTime end = appointment.getAppointmentEndDate();

        // Check clinic availability - need to implement proper date range check
        // For now, just count appointments for the clinic on that date
        
        // Calculate if we're at overbooking threshold
        long existingCount = appointmentRepository
            .countByClinicIdAndAppointmentDateBetween(
                appointment.getClinicId(), 
                start.toLocalDate().atStartOfDay(), 
                start.toLocalDate().atTime(23, 59));

        // Get clinic overbooking threshold
        Optional<Clinic> clinic = clinicRepository.findById(appointment.getClinicId());
        int threshold = clinic.map(Clinic::getOverbookingThreshold).orElse(2);

        if (existingCount >= threshold) {
            throw new IllegalStateException("Clinic is at overbooking threshold for this time slot");
        }

        // Check provider availability if provider is specified
        if (appointment.getProviderId() != null) {
            List<Appointment> providerAppointments = appointmentRepository
                .findByProviderIdAndAppointmentDateBetween(appointment.getProviderId(), start, end);

            if (!providerAppointments.isEmpty()) {
                throw new IllegalStateException("Provider is already booked at this time");
            }
        }
    }

    /**
     * Generate unique appointment number.
     */
    private String generateAppointmentNumber(Appointment appointment) {
        return "APT-" + 
               appointment.getAppointmentDate().getYear() + 
               String.format("%02d", appointment.getAppointmentDate().getMonthValue()) +
               String.format("%02d", appointment.getAppointmentDate().getDayOfMonth()) +
               "-" + 
               System.currentTimeMillis() % 100000;
    }

    /**
     * Check-in patient to appointment.
     */
    @Transactional
    public void checkIn(String appointmentId, Authentication authentication) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new IllegalStateException("Appointment not found"));

        appointment.setStatus("CHECKED_IN");
        appointment.setCheckInTime(java.time.Instant.now());

        appointmentRepository.save(appointment);

        log.info("Checked in appointment: {} by user: {}", appointmentId, authentication.getName());
    }

    /**
     * Reschedule appointment.
     */
    @Transactional
    public Appointment reschedule(String appointmentId, LocalDateTime newDateTime, Authentication authentication) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new IllegalStateException("Appointment not found"));

        // Update appointment time
        appointment.setAppointmentDate(newDateTime);
        appointment.setAppointmentEndDate(newDateTime.plusMinutes(appointment.getDurationMinutes()));

        // Check for conflicts with new time
        checkForConflicts(appointment);

        return appointmentRepository.save(appointment);
    }

    /**
     * Cancel appointment.
     */
    @Transactional
    public void cancel(String appointmentId, String reason, Authentication authentication) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new IllegalStateException("Appointment not found"));

        appointment.setStatus("CANCELLED");
        appointment.setNotes((appointment.getNotes() != null ? appointment.getNotes() + ". " : "") + 
            "Cancelled: " + reason);

        appointmentRepository.save(appointment);

        log.info("Cancelled appointment: {} by user: {} reason: {}", appointmentId, authentication.getName(), reason);
    }
}

