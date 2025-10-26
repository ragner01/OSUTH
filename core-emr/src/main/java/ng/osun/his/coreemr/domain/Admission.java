package ng.osun.his.coreemr.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

/**
 * Admission entity for IPD patients.
 */
@Entity
@Table(name = "admissions", indexes = {
    @Index(name = "idx_admissions_patient", columnList = "patient_id"),
    @Index(name = "idx_admissions_bed", columnList = "bed_id"),
    @Index(name = "idx_admissions_number", columnList = "admission_number")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Admission extends BaseEntity {

    @Column(name = "admission_number", unique = true, nullable = false, length = 50)
    private String admissionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bed_id", nullable = false)
    private Bed bed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id")
    private Encounter encounter;

    @Column(name = "admission_date", nullable = false)
    private java.time.Instant admissionDate;

    @Column(name = "discharge_date")
    private java.time.Instant dischargeDate;

    @Column(name = "admission_type", length = 50)
    private String admissionType; // ELECTIVE, EMERGENCY, TRANSFER

    @Column(name = "admitting_physician_id", length = 100)
    private String admittingPhysicianId;

    @Column(name = "admitting_physician_name", length = 200)
    private String admittingPhysicianName;

    @Column(name = "discharging_physician_id", length = 100)
    private String dischargingPhysicianId;

    @Column(name = "discharging_physician_name", length = 200)
    private String dischargingPhysicianName;

    @Column(name = "discharge_summary", columnDefinition = "TEXT")
    private String dischargeSummary;

    @Column(name = "status", length = 50)
    private String status; // ADMITTED, DISCHARGED, TRANSFERRED
}

