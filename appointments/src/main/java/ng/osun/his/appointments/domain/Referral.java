package ng.osun.his.appointments.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

import java.util.List;

/**
 * Referral entity for internal and external referrals.
 */
@Entity
@Table(name = "referrals", indexes = {
    @Index(name = "idx_referrals_patient", columnList = "patient_id"),
    @Index(name = "idx_referrals_status", columnList = "status")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Referral extends BaseEntity {

    @Column(name = "referral_number", unique = true, length = 50)
    private String referralNumber;

    @Column(name = "patient_id", nullable = false, length = 36)
    private String patientId;

    @Column(name = "type", nullable = false, length = 50)
    private String type; // INTERNAL, EXTERNAL

    @Column(name = "from_provider_id", length = 36)
    private String fromProviderId;

    @Column(name = "from_provider_name", length = 200)
    private String fromProviderName;

    @Column(name = "to_clinic_id", length = 36)
    private String toClinicId;

    @Column(name = "to_provider_id", length = 36)
    private String toProviderId;

    @Column(name = "to_facility_name", length = 200)
    private String toFacilityName;

    @Column(name = "reason", length = 1000)
    private String reason;

    @Column(name = "clinical_summary", columnDefinition = "TEXT")
    private String clinicalSummary;

    @ElementCollection
    @CollectionTable(name = "referral_attachments", joinColumns = @JoinColumn(name = "referral_id"))
    private List<AttachmentMetadata> attachments;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // PENDING, SCHEDULED, COMPLETED, CANCELLED

    @Column(name = "priority", length = 50)
    private String priority; // URGENT, ROUTINE

    @Column(name = "referral_date")
    private java.time.LocalDate referralDate;

    @Column(name = "scheduled_date")
    private java.time.LocalDateTime scheduledDate;

    @Column(name = "completed_date")
    private java.time.LocalDateTime completedDate;
}

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
class AttachmentMetadata {
    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "storage_path")
    private String storagePath;
}

