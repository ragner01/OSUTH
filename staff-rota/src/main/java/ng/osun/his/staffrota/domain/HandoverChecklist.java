package ng.osun.his.staffrota.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

import java.util.List;

/**
 * Handover checklist for safe patient transfer.
 */
@Entity
@Table(name = "handover_checklists", indexes = {
    @Index(name = "idx_handover_patient", columnList = "patient_id"),
    @Index(name = "idx_handover_status", columnList = "status"),
    @Index(name = "idx_handover_encounter", columnList = "encounter_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class HandoverChecklist extends BaseEntity {

    @Column(name = "patient_id", nullable = false, length = 36)
    private String patientId;

    @Column(name = "encounter_id", length = 36)
    private String encounterId;

    @Column(name = "handover_type", nullable = false, length = 50)
    private String handoverType; // SHIFT_TO_SHIFT, WARD_TO_WARD, DISCHARGE, EMERGENCY

    @Column(name = "from_location", length = 100)
    private String fromLocation; // Ward, department

    @Column(name = "to_location", length = 100)
    private String toLocation;

    @Column(name = "from_staff_id", length = 36)
    private String fromStaffId;

    @Column(name = "to_staff_id", length = 36)
    private String toStaffId;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // IN_PROGRESS, COMPLETED, BLOCKED

    @Column(name = "completed_at")
    private java.time.Instant completedAt;

    @Column(name = "verified_by", length = 100)
    private String verifiedBy;

    @ElementCollection
    @CollectionTable(name = "handover_items", joinColumns = @JoinColumn(name = "checklist_id"))
    private List<HandoverItem> items;

    @Column(name = "critical_findings", columnDefinition = "TEXT")
    private String criticalFindings;

    @Column(name = "infection_control_flags", length = 200)
    private String infectionControlFlags; // ISOLATION, CONTACT_PRECAUTIONS, etc.
}
