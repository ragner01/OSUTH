package ng.osun.his.appointments.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

/**
 * Clinic entity representing a department or clinic.
 */
@Entity
@Table(name = "clinics")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Clinic extends BaseEntity {

    @Column(name = "code", unique = true, length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @ElementCollection
    @CollectionTable(name = "clinic_hours", joinColumns = @JoinColumn(name = "clinic_id"))
    private Set<OperatingHours> hours;

    @ElementCollection
    @CollectionTable(name = "clinic_blackout_dates", joinColumns = @JoinColumn(name = "clinic_id"))
    @Column(name = "date")
    private Set<java.time.LocalDate> blackoutDates;

    @Column(name = "slot_duration_minutes")
    private Integer slotDurationMinutes = 30;

    @Column(name = "overbooking_threshold")
    private Integer overbookingThreshold = 2;

    @Column(name = "active")
    private Boolean active = true;
}

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
class OperatingHours {
    @Column(name = "day_of_week")
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;
}

