package ng.osun.his.appointments.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

/**
 * Queue item embeddable for managing patient queue positions.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueItem {
    @Column(name = "patient_id")
    private String patientId;

    @Column(name = "appointment_id")
    private String appointmentId;

    @Column(name = "priority")
    private Integer priority; // 1-5, lower is higher priority

    @Column(name = "eta_minutes")
    private Integer etaMinutes;

    @Column(name = "status")
    private String status; // WAITING, IN_PROGRESS, COMPLETED, CANCELLED

    @Column(name = "queued_at")
    private Instant queuedAt;

    @Column(name = "estimated_start_time")
    private Instant estimatedStartTime;
}

