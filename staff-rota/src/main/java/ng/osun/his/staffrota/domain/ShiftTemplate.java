package ng.osun.his.staffrota.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

/**
 * Shift template for standard shift patterns.
 */
@Entity
@Table(name = "shift_templates")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ShiftTemplate extends BaseEntity {

    @Column(name = "template_name", nullable = false, length = 100)
    private String templateName;

    @Column(name = "start_time", nullable = false)
    private java.time.LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private java.time.LocalTime endTime;

    @Column(name = "duration_hours", nullable = false)
    private Integer durationHours;

    @Column(name = "shift_type", nullable = false, length = 50)
    private String shiftType; // MORNING, AFTERNOON, NIGHT, FLOAT

    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @Column(name = "required_staff_count")
    private Integer requiredStaffCount;

    @Column(name = "break_duration_minutes")
    private Integer breakDurationMinutes;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}

