package ng.osun.his.staffrota.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

/**
 * Patient transfer entity for bed-to-bed movement.
 */
@Entity
@Table(name = "transfers", indexes = {
    @Index(name = "idx_transfer_patient", columnList = "patient_id"),
    @Index(name = "idx_transfer_status", columnList = "status"),
    @Index(name = "idx_transfer_dates", columnList = "transfer_date")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Transfer extends BaseEntity {

    @Column(name = "transfer_number", unique = true, length = 50)
    private String transferNumber;

    @Column(name = "patient_id", nullable = false, length = 36)
    private String patientId;

    @Column(name = "from_ward_id", nullable = false, length = 36)
    private String fromWardId;

    @Column(name = "from_ward_name", length = 100)
    private String fromWardName;

    @Column(name = "from_bed_id", length = 36)
    private String fromBedId;

    @Column(name = "from_bed_label", length = 50)
    private String fromBedLabel;

    @Column(name = "to_ward_id", nullable = false, length = 36)
    private String toWardId;

    @Column(name = "to_ward_name", length = 100)
    private String toWardName;

    @Column(name = "to_bed_id", length = 36)
    private String toBedId;

    @Column(name = "to_bed_label", length = 50)
    private String toBedLabel;

    @Column(name = "transfer_reason", nullable = false, length = 500)
    private String transferReason;

    @Column(name = "clinical_condition", columnDefinition = "TEXT")
    private String clinicalCondition;

    @Column(name = "transfer_date", nullable = false)
    private java.time.LocalDate transferDate;

    @Column(name = "transfer_time")
    private java.time.LocalTime transferTime;

    @Column(name = "transferred_by", length = 100)
    private String transferredBy;

    @Column(name = "received_by", length = 100)
    private String receivedBy;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED

    @Column(name = "notes", length = 1000)
    private String notes;
}

