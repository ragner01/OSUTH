package ng.osun.his.pharmacy.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

import java.util.List;

/**
 * Prescription entity with status workflow.
 */
@Entity
@Table(name = "prescriptions", indexes = {
    @Index(name = "idx_prescriptions_patient", columnList = "patient_id"),
    @Index(name = "idx_prescriptions_encounter", columnList = "encounter_id"),
    @Index(name = "idx_prescriptions_status", columnList = "status")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Prescription extends BaseEntity {

    @Column(name = "prescription_number", unique = true, length = 50)
    private String prescriptionNumber;

    @Column(name = "patient_id", nullable = false, length = 36)
    private String patientId;

    @Column(name = "encounter_id", nullable = false, length = 36)
    private String encounterId;

    @ElementCollection
    @CollectionTable(name = "prescription_items", joinColumns = @JoinColumn(name = "prescription_id"))
    private List<PrescriptionItem> items;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // PRESCRIBED, DISPENSED, CANCELLED, EXPIRED

    @Column(name = "prescribed_by", nullable = false, length = 100)
    private String prescribedBy;

    @Column(name = "prescribed_by_name", length = 200)
    private String prescribedByName;

    @Column(name = "prescribed_date", nullable = false)
    private java.time.Instant prescribedDate;

    @Column(name = "valid_until")
    private java.time.Instant validUntil;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "allergy_override")
    private Boolean allergyOverride = false;

    @Column(name = "allergy_override_reason", length = 500)
    private String allergyOverrideReason;

    @Column(name = "allergy_override_by", length = 100)
    private String allergyOverrideBy;

    @Column(name = "interaction_override")
    private Boolean interactionOverride = false;

    @Column(name = "interaction_override_reason", length = 500)
    private String interactionOverrideReason;

    @Column(name = "interaction_override_by", length = 100)
    private String interactionOverrideBy;
}

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
class PrescriptionItem {
    @Column(name = "medication_code")
    private String medicationCode;

    @Column(name = "medication_name")
    private String medicationName;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "sig")
    private String sig; // Instructions: "Take 1 tablet twice daily"

    @Column(name = "route")
    private String route;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "repeats")
    private Integer repeats = 0;

    @Column(name = "additional_instructions")
    private String additionalInstructions;
}

