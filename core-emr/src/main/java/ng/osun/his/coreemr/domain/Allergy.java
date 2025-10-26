package ng.osun.his.coreemr.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

/**
 * Patient allergy entity.
 */
@Entity
@Table(name = "allergies", indexes = {
    @Index(name = "idx_allergies_patient", columnList = "patient_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Allergy extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "substance", nullable = false, length = 200)
    private String substance; // e.g., "Penicillin", "Shellfish"

    @Column(name = "reaction", nullable = false, length = 500)
    private String reaction; // e.g., "Rash", "Anaphylaxis"

    @Column(name = "severity", length = 50)
    private String severity; // MILD, MODERATE, SEVERE

    @Column(name = "status", length = 50)
    private String status; // ACTIVE, INACTIVE, RESOLVED

    @Column(name = "onset_date")
    private java.time.LocalDate onsetDate;

    @Column(name = "last_observed")
    private java.time.LocalDate lastObserved;

    @Column(name = "notes", length = 1000)
    private String notes;
}

