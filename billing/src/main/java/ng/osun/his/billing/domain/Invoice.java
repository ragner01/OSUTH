package ng.osun.his.billing.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * Invoice entity for patient bills.
 */
@Entity
@Table(name = "invoices", indexes = {
    @Index(name = "idx_invoice_patient", columnList = "patient_id"),
    @Index(name = "idx_invoice_payer", columnList = "payer_id"),
    @Index(name = "idx_invoice_status", columnList = "status"),
    @Index(name = "idx_invoice_date", columnList = "invoice_date")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Invoice extends BaseEntity {

    @Column(name = "invoice_number", unique = true, nullable = false, length = 50)
    private String invoiceNumber;

    @Column(name = "patient_id", nullable = false, length = 36)
    private String patientId;

    @Column(name = "encounter_id", length = 36)
    private String encounterId;

    @Column(name = "payer_id", length = 36)
    private String payerId; // null = self-pay

    @Column(name = "payer_type", length = 50)
    private String payerType; // SELF_PAY, NHIA, HMO

    @Column(name = "payer_name", length = 200)
    private String payerName;

    @Column(name = "invoice_date", nullable = false)
    private java.time.LocalDate invoiceDate;

    @Column(name = "due_date")
    private java.time.LocalDate dueDate;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // DRAFT, PENDING, PARTIAL, PAID, CANCELLED

    @Column(name = "subtotal_ngn", nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotalNGN;

    @Column(name = "discount_amount_ngn", precision = 19, scale = 2)
    private BigDecimal discountAmountNGN = BigDecimal.ZERO;

    @Column(name = "tax_amount_ngn", precision = 19, scale = 2)
    private BigDecimal taxAmountNGN = BigDecimal.ZERO;

    @Column(name = "total_amount_ngn", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmountNGN;

    @Column(name = "paid_amount_ngn", precision = 19, scale = 2)
    private BigDecimal paidAmountNGN = BigDecimal.ZERO;

    @Column(name = "balance_ngn", nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceNGN;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "NGN";

    @Column(name = "payment_terms", length = 100)
    private String paymentTerms;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @ElementCollection
    @CollectionTable(name = "invoice_line_items", joinColumns = @JoinColumn(name = "invoice_id"))
    private List<InvoiceLineItem> lineItems;
}

