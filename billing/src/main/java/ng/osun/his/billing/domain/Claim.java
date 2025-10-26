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
 * Health insurance claim entity.
 */
@Entity
@Table(name = "claims", indexes = {
    @Index(name = "idx_claim_patient", columnList = "patient_id"),
    @Index(name = "idx_claim_payer", columnList = "payer_id"),
    @Index(name = "idx_claim_status", columnList = "status"),
    @Index(name = "idx_claim_number", columnList = "claim_number")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Claim extends BaseEntity {

    @Column(name = "claim_number", unique = true, nullable = false, length = 50)
    private String claimNumber;

    @Column(name = "patient_id", nullable = false, length = 36)
    private String patientId;

    @Column(name = "payer_id", nullable = false, length = 36)
    private String payerId;

    @Column(name = "payer_name", length = 200)
    private String payerName;

    @Column(name = "invoice_id", nullable = false, length = 36)
    private String invoiceId;

    @Column(name = "claim_date", nullable = false)
    private java.time.LocalDate claimDate;

    @Column(name = "submission_date")
    private java.time.Instant submissionDate;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // DRAFT, SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, PAID

    @Column(name = "total_amount_ngn", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmountNGN;

    @Column(name = "approved_amount_ngn", precision = 19, scale = 2)
    private BigDecimal approvedAmountNGN;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "NGN";

    @Column(name = "review_notes", columnDefinition = "TEXT")
    private String reviewNotes;

    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;

    @Column(name = "response_received_at")
    private java.time.Instant responseReceivedAt;

    @Column(name = "submitted_by", length = 100)
    private String submittedBy;

    @ElementCollection
    @CollectionTable(name = "claim_attachments", joinColumns = @JoinColumn(name = "claim_id"))
    private List<ClaimAttachment> attachments;
}

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
class ClaimAttachment {
    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "storage_path")
    private String storagePath;

    @Column(name = "uploaded_at")
    private java.time.Instant uploadedAt;
}

