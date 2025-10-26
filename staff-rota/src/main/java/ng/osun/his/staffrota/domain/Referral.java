package ng.osun.his.staffrota.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

import java.util.List;

/**
 * Patient referral entity.
 */
@Entity
@Table(name = "referrals", indexes = {
    @Index(name = "idx_referral_patient", columnList = "patient_id"),
    @Index(name = "idx_referral_status", columnList = "status"),
    @Index(name = "idx_referral_from", columnList = "from_provider_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Referral extends BaseEntity {

    @Column(name = "referral_number", unique = true, nullable = false, length = 50)
    private String referralNumber;

    @Column(name = "patient_id", nullable = false, length = 36)
    private String patientId;

    @Column(name = "from_provider_id", nullable = false, length = 36)
    private String fromProviderId;

    @Column(name = "from_provider_name", length = 200)
    private String fromProviderName;

    @Column(name = "from_department", length = 100)
    private String fromDepartment;

    @Column(name = "to_provider_id", length = 36)
    private String toProviderId;

    @Column(name = "to_provider_name", length = 200)
    private String toProviderName;

    @Column(name = "to_department", nullable = false, length = 100)
    private String toDepartment;

    @Column(name = "referral_type", nullable = false, length = 50)
    private String referralType; // INTERNAL, EXTERNAL

    @Column(name = "urgency", length = 50)
    private String urgency; // ROUTINE, URGENT, EMERGENCY

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "clinical_summary", columnDefinition = "TEXT")
    private String clinicalSummary;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // PENDING, ACCEPTED, REJECTED, COMPLETED, CANCELLED

    @Column(name = "referral_date", nullable = false)
    private java.time.LocalDate referralDate;

    @Column(name = "expiry_date")
    private java.time.LocalDate expiryDate;

    @Column(name = "accepted_at")
    private java.time.Instant acceptedAt;

    @Column(name = "accepted_by", length = 100)
    private String acceptedBy;

    @ElementCollection
    @CollectionTable(name = "referral_attachments", joinColumns = @JoinColumn(name = "referral_id"))
    private List<ReferralAttachment> attachments;
}

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
class ReferralAttachment {
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

