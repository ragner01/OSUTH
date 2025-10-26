package ng.osun.his.coreemr.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

/**
 * Patient encounter entity (OPD, IPD, ED).
 */
@Entity
@Table(name = "encounters", indexes = {
    @Index(name = "idx_encounters_patient", columnList = "patient_id"),
    @Index(name = "idx_encounters_status", columnList = "status")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Encounter extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "encounter_number", unique = true, length = 50)
    private String encounterNumber;

    @Column(name = "type", nullable = false, length = 20)
    private String type; // OPD, IPD, ED

    @Column(name = "status", nullable = false, length = 20)
    private String status; // IN_PROGRESS, COMPLETED, CANCELLED

    @Column(name = "start_date", nullable = false)
    private java.time.Instant startDate;

    @Column(name = "end_date")
    private java.time.Instant endDate;

    @Column(name = "reason", length = 500)
    private String reason; // Chief complaint

    @Column(name = "location", length = 100)
    private String location; // Ward/Department

    @Column(name = "clinician_id", length = 100)
    private String clinicianId;

    @Column(name = "clinician_name", length = 200)
    private String clinicianName;

    @Column(name = "triage_level", length = 20)
    private String triageLevel; // 1=Critical, 2=Urgent, 3=Non-urgent

    @Column(name = "admission_id")
    private String admissionId; // Link to admission for IPD encounters

    @Column(name = "visit_type", length = 50)
    private String visitType; // First Visit, Follow-up, Emergency

    @Column(name = "referral_source", length = 200)
    private String referralSource;

    @Column(name = "final_diagnosis", length = 1000)
    private String finalDiagnosis;
}

