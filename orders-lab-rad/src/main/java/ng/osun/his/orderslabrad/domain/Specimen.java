package ng.osun.his.orderslabrad.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

/**
 * Specimen entity for lab samples.
 */
@Entity
@Table(name = "specimens", indexes = {
    @Index(name = "idx_specimens_order", columnList = "order_item_id"),
    @Index(name = "idx_specimens_status", columnList = "status")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Specimen extends BaseEntity {

    @Column(name = "specimen_number", unique = true, length = 50)
    private String specimenNumber;

    @Column(name = "order_id", nullable = false, length = 36)
    private String orderId;

    @Column(name = "order_item_id", nullable = false, length = 36)
    private String orderItemId;

    @Column(name = "patient_id", nullable = false, length = 36)
    private String patientId;

    @Column(name = "type", nullable = false, length = 100)
    private String type; // Blood, Urine, Stool, Sputum, etc.

    @Column(name = "container", length = 100)
    private String container; // EDTA Tube, Sterile Container, etc.

    @Column(name = "collected_at", nullable = false)
    private java.time.Instant collectedAt;

    @Column(name = "collector_id", nullable = false, length = 100)
    private String collectorId;

    @Column(name = "collector_name", length = 200)
    private String collectorName;

    @Column(name = "received_at")
    private java.time.Instant receivedAt;

    @Column(name = "received_by", length = 100)
    private String receivedBy;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // COLLECTED, TRANSPORTED, RECEIVED, IN_PROGRESS, REJECTED

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "quantity", length = 50)
    private String quantity; // Volume, size, etc.

    @Column(name = "storage_conditions", length = 200)
    private String storageConditions;

    @Column(name = "expiry_time")
    private java.time.Instant expiryTime;
}

