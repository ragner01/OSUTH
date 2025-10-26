package ng.osun.his.billing.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * Payment entity for invoice settlements.
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_invoice", columnList = "invoice_id"),
    @Index(name = "idx_payment_patient", columnList = "patient_id"),
    @Index(name = "idx_payment_date", columnList = "payment_date")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

    @Column(name = "payment_number", unique = true, length = 50)
    private String paymentNumber;

    @Column(name = "invoice_id", nullable = false, length = 36)
    private String invoiceId;

    @Column(name = "patient_id", nullable = false, length = 36)
    private String patientId;

    @Column(name = "payment_date", nullable = false)
    private java.time.LocalDate paymentDate;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod; // CASH, CARD, BANK_TRANSFER, MOBILE_MONEY, CHEQUE

    @Column(name = "amount_ngn", nullable = false, precision = 19, scale = 2)
    private BigDecimal amountNGN;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "NGN";

    @Column(name = "reference", length = 100)
    private String reference; // transaction reference, cheque number

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // PENDING, CONFIRMED, REVERSED

    @Column(name = "confirmed_at")
    private java.time.Instant confirmedAt;

    @Column(name = "confirmed_by", length = 100)
    private String confirmedBy;

    @Column(name = "cashier", length = 100)
    private String cashier;

    @Column(name = "notes", length = 500)
    private String notes;
}

