package ng.osun.his.coreemr.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

/**
 * Diagnosis entity (ICD-10 based).
 */
@Entity
@Table(name = "diagnoses", indexes = {
    @Index(name = "idx_diagnoses_encounter", columnList = "encounter_id"),
    @Index(name = "idx_diagnoses_patient", columnList = "patient_id"),
    @Index(name = "idx_diagnoses_code", columnList = "code")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Diagnosis extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    private Encounter encounter;

    @Column(name = "code", nullable = false, length = 20)
    private String code; // ICD-10 code

    @Column(name = "system", length = 50)
    private String system; // ICD-10, ICD-11, SNOMED

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "type", nullable = false, length = 50)
    private String type; // PRIMARY, SECONDARY

    @Column(name = "certainty", length = 50)
    private String certainty; // CONFIRMED, PROVISIONAL, RULED_OUT

    @Column(name = "onset_date")
    private java.time.LocalDate onsetDate;

    @Column(name = "resolved_date")
    private java.time.LocalDate resolvedDate;

    @Column(name = "status", length = 50)
    private String status; // ACTIVE, RESOLVED, CHRONIC

    @Column(name = "notes", length = 1000)
    private String notes;
}

