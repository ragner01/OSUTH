package ng.osun.his.appointments.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

/**
 * Appointment entity.
 */
@Entity
@Table(name = "appointments", indexes = {
    @Index(name = "idx_appointments_patient", columnList = "patient_id"),
    @Index(name = "idx_appointments_clinic", columnList = "clinic_id"),
    @Index(name = "idx_appointments_provider", columnList = "provider_id"),
    @Index(name = "idx_appointments_date", columnList = "appointment_date"),
    @Index(name = "idx_appointments_status", columnList = "status")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Appointment extends BaseEntity {

    @Column(name = "appointment_number", unique = true, length = 50)
    private String appointmentNumber;

    @Column(name = "patient_id", nullable = false, length = 36)
    private String patientId;

    @Column(name = "clinic_id", nullable = false, length = 36)
    private String clinicId;

    @Column(name = "provider_id", length = 36)
    private String providerId;

    @Column(name = "appointment_date", nullable = false)
    private java.time.LocalDateTime appointmentDate;

    @Column(name = "appointment_end_date")
    private java.time.LocalDateTime appointmentEndDate;

    @Column(name = "duration_minutes")
    private Integer durationMinutes = 30;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // SCHEDULED, CONFIRMED, CHECKED_IN, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW

    @Column(name = "type", length = 50)
    private String type; // WALK_IN, REFERRAL, FOLLOW_UP, REVIEW

    @Column(name = "source", length = 50)
    private String source; // CLINIC, PHONE, ONLINE, REFERRAL

    @Column(name = "chief_complaint", length = 500)
    private String chiefComplaint;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "referral_id", length = 36)
    private String referralId;

    @Column(name = "sms_reminder_sent")
    private Boolean smsReminderSent = false;

    @Column(name = "reminder_sent_at")
    private java.time.Instant reminderSentAt;

    @Column(name = "check_in_time")
    private java.time.Instant checkInTime;

    @Column(name = "start_time")
    private java.time.Instant startTime;

    @Column(name = "end_time")
    private java.time.Instant endTime;
}

