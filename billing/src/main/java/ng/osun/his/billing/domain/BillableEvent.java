package ng.osun.his.billing.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * Billable events generated from clinical activities.
 */
@Entity
@Table(name = "billable_events", indexes = {
    @Index(name = "idx_billable_patient", columnList = "patient_id"),
    @Index(name = "idx_billable_source", columnList = "source_module"),
    @Index(name = "idx_billable_invoiced", columnList = "invoice_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class BillableEvent extends BaseEntity {

    @Column(name = "event_number", unique = true, length = 50)
    private String eventNumber;

    @Column(name = "patient_id", nullable = false, length = 36)
    private String patientId;

    @Column(name = "encounter_id", length = 36)
    private String encounterId;

    @Column(name = "source_module", nullable = false, length = 50)
    private String sourceModule; // CONSULTATION, LAB, RADIOLOGY, PHARMACY

    @Column(name = "source_ref_id", nullable = false, length = 100)
    private String sourceRefId; // appointment_id, order_id, prescription_id

    @Column(name = "service_code", nullable = false, length = 50)
    private String serviceCode;

    @Column(name = "service_description", length = 500)
    private String serviceDescription;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "unit_price_ngn", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPriceNGN;

    @Column(name = "total_amount_ngn", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmountNGN;

    @Column(name = "discount_amount_ngn", precision = 19, scale = 2)
    private BigDecimal discountAmountNGN = BigDecimal.ZERO;

    @Column(name = "net_amount_ngn", nullable = false, precision = 19, scale = 2)
    private BigDecimal netAmountNGN;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "NGN";

    @Column(name = "payer_id", length = 36)
    private String payerId; // null for self-pay

    @Column(name = "invoice_id", length = 36)
    private String invoiceId;

    @Column(name = "billed", nullable = false)
    private Boolean billed = false;

    @Column(name = "billed_at")
    private java.time.Instant billedAt;

    @Column(name = "billed_by", length = 100)
    private String billedBy;
}

