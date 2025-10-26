package ng.osun.his.appointments.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

/**
 * Healthcare provider entity.
 */
@Entity
@Table(name = "providers")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Provider extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true, length = 100)
    private String userId; // Reference to Keycloak user

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "title", length = 50)
    private String title; // Dr., Nurse, etc.

    @Column(name = "specialty", length = 100)
    private String specialty;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "license_number", length = 50)
    private String licenseNumber;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "daily_capacity")
    private Integer dailyCapacity;

    @Column(name = "consultation_duration_minutes")
    private Integer consultationDurationMinutes = 30;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "on_duty")
    private Boolean onDuty = false;
}

