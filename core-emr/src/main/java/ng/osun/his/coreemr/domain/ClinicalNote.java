package ng.osun.his.coreemr.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

/**
 * Clinical notes entity.
 */
@Entity
@Table(name = "clinical_notes", indexes = {
    @Index(name = "idx_notes_encounter", columnList = "encounter_id"),
    @Index(name = "idx_notes_patient", columnList = "patient_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalNote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    private Encounter encounter;

    @Column(name = "type", nullable = false, length = 50)
    private String type; // PROGRESS_NOTE, SOAP, CONSULTATION, DISCHARGE

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @ElementCollection
    @CollectionTable(name = "clinical_note_attachments", joinColumns = @JoinColumn(name = "note_id"))
    private java.util.List<AttachmentMetadata> attachments;

    @Column(name = "author_id", nullable = false, length = 100)
    private String authorId;

    @Column(name = "author_name", length = 200)
    private String authorName;

    @Column(name = "written_at", nullable = false)
    private java.time.Instant writtenAt;

    @Column(name = "status", length = 50)
    private String status; // DRAFT, FINAL, SIGNED
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

    @Column(name = "uploaded_at")
    private java.time.Instant uploadedAt;
}

