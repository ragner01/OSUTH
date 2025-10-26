package ng.osun.his.staffrota.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

/**
 * Staff entity for workforce management.
 */
@Entity
@Table(name = "staff", indexes = {
    @Index(name = "idx_staff_user_ref", columnList = "user_ref"),
    @Index(name = "idx_staff_department", columnList = "department"),
    @Index(name = "idx_staff_role", columnList = "role")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Staff extends BaseEntity {

    @Column(name = "user_ref", unique = true, length = 36)
    private String userRef; // Reference to identity service user

    @Column(name = "employee_number", unique = true, nullable = false, length = 50)
    private String employeeNumber;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "role", nullable = false, length = 50)
    private String role; // DOCTOR, NURSE, PHARMACIST, LAB_SCIENTIST, ADMIN, etc.

    @Column(name = "specialty", length = 100)
    private String specialty; // For doctors

    @Column(name = "department", nullable = false, length = 100)
    private String department; // EMERGENCY, SURGERY, PEDIATRICS, etc.

    @Column(name = "level", length = 50)
    private String level; // JUNIOR, SENIOR, CONSULTANT, etc.

    @Column(name = "license_number", length = 100)
    private String licenseNumber;

    @Column(name = "employment_date")
    private java.time.LocalDate employmentDate;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}

