package ng.osun.his.orderslabrad.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Order entity for lab/radiology orders.
 */
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_orders_patient", columnList = "patient_id"),
    @Index(name = "idx_orders_encounter", columnList = "encounter_id"),
    @Index(name = "idx_orders_status", columnList = "status")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {

    @Column(name = "order_number", unique = true, length = 50)
    private String orderNumber;

    @Column(name = "patient_id", nullable = false, length = 36)
    private String patientId;

    @Column(name = "encounter_id", nullable = false, length = 36)
    private String encounterId;

    @Column(name = "type", nullable = false, length = 20)
    private String type; // LAB, RAD

    @Column(name = "status", nullable = false, length = 50)
    private String status; // PLACED, IN_PROGRESS, RESULTED, VERIFIED, CANCELLED

    @Column(name = "priority", length = 50)
    private String priority; // ROUTINE, URGENT, STAT

    @Column(name = "ordered_by", nullable = false, length = 100)
    private String orderedBy;

    @Column(name = "ordered_by_name", length = 200)
    private String orderedByName;

    @Column(name = "order_date", nullable = false)
    private java.time.Instant orderDate;

    @Column(name = "started_date")
    private java.time.Instant startedDate;

    @Column(name = "resulted_date")
    private java.time.Instant resultedDate;

    @Column(name = "verified_date")
    private java.time.Instant verifiedDate;

    @Column(name = "clinical_indication", length = 500)
    private String clinicalIndication;

    @ElementCollection
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderItem> items;

    @Column(name = "comments", length = 1000)
    private String comments;
}

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
class OrderItem {
    @Column(name = "test_code")
    private String testCode; // LOINC code

    @Column(name = "test_name")
    private String testName;

    @Column(name = "panel")
    private String panel; // CBC, LFT, etc.

    @Column(name = "priority")
    private String priority; // ROUTINE, URGENT, STAT

    @Column(name = "clinician_notes", length = 500)
    private String clinicianNotes;

    @Column(name = "expected_result_at")
    private java.time.Instant expectedResultAt;
}

