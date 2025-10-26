package ng.osun.his.orderslabrad.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;
import java.util.List;

/**
 * Imaging study entity for radiology.
 */
@Entity
@Table(name = "imaging_studies", indexes = {
    @Index(name = "idx_imaging_order", columnList = "order_id"),
    @Index(name = "idx_imaging_patient", columnList = "patient_id"),
    @Index(name = "idx_imaging_modality", columnList = "modality")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ImagingStudy extends BaseEntity {

    @Column(name = "study_number", unique = true, length = 50)
    private String studyNumber;

    @Column(name = "order_id", nullable = false, length = 36)
    private String orderId;

    @Column(name = "patient_id", nullable = false, length = 36)
    private String patientId;

    @Column(name = "modality", nullable = false, length = 50)
    private String modality; // X-RAY, CT, MRI, ULTRASOUND, MAMMOGRAPHY, etc.

    @Column(name = "body_part", nullable = false, length = 100)
    private String bodyPart; // Chest, Abdomen, Head, etc.

    @Column(name = "accession_number", length = 50)
    private String accessionNumber;

    @Column(name = "study_date", nullable = false)
    private java.time.Instant studyDate;

    @Column(name = "performed_by", length = 100)
    private String performedBy;

    @Column(name = "performed_by_name", length = 200)
    private String performedByName;

    @Column(name = "interpreted_by", length = 100)
    private String interpretedBy;

    @Column(name = "interpreted_by_name", length = 200)
    private String interpretedByName;

    @Column(name = "interpreted_at")
    private java.time.Instant interpretedAt;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // REGISTERED, IN_PROGRESS, COMPLETED, VERIFIED

    @ElementCollection
    @CollectionTable(name = "imaging_report_attachments", joinColumns = @JoinColumn(name = "study_id"))
    private List<ReportAttachmentMetadata> reportAttachments;

    @Column(name = "radiologist_findings", columnDefinition = "TEXT")
    private String radiologistFindings;

    @Column(name = "impression", columnDefinition = "TEXT")
    private String impression;

    @Column(name = "recommendations", length = 1000)
    private String recommendations;
}

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
class ReportAttachmentMetadata {
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

    @Column(name = "uploaded_by")
    private String uploadedBy;
}

