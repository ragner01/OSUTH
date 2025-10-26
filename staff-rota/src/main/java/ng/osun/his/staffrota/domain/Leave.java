package ng.osun.his.staffrota.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

/**
 * Leave application entity.
 */
@Entity
@Table(name = "leaves", indexes = {
    @Index(name = "idx_leave_staff", columnList = "staff_id"),
    @Index(name = "idx_leave_status", columnList = "status"),
    @Index(name = "idx_leave_dates", columnList = "start_date, end_date")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Leave extends BaseEntity {

    @Column(name = "staff_id", nullable = false, length = 36)
    private String staffId;

    @Column(name = "leave_type", nullable = false, length = 50)
    private String leaveType; // ANNUAL, SICK, MATERNITY, COMPASSIONATE, STUDY

    @Column(name = "start_date", nullable = false)
    private java.time.LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private java.time.LocalDate endDate;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // PENDING, APPROVED, REJECTED, CANCELLED

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "applied_by", length = 100)
    private String appliedBy;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "approved_at")
    private java.time.Instant approvedAt;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;
}

