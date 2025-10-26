package ng.osun.his.coreemr.domain;

import lombok.*;
import ng.osun.his.platform.domain.BaseEntity;
import ng.osun.his.platform.domain.FacilityContext;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Patient entity with multi-facility support.
 */
@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Patient extends BaseEntity {

    // Multi-facility support
    @Column(name = "facility_id", nullable = false, length = 50)
    private String facilityId;

    @Column(name = "mrn", length = 20)
    private String mrn;

    @Column(name = "nin", length = 11)
    private String nin;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "primary_phone", length = 20)
    private String primaryPhone;

    @Column(name = "email")
    private String email;

    @Column(name = "residential_address", columnDefinition = "TEXT")
    private String residentialAddress;

    @ElementCollection
    @CollectionTable(name = "patient_addresses", joinColumns = @JoinColumn(name = "patient_id"))
    private List<PatientAddress> addresses;

    @ElementCollection
    @CollectionTable(name = "patient_phones", joinColumns = @JoinColumn(name = "patient_id"))
    private List<String> phones;

    @ElementCollection
    @CollectionTable(name = "patient_identifiers", joinColumns = @JoinColumn(name = "patient_id"))
    private List<PatientIdentifier> identifiers;

    @ElementCollection
    @CollectionTable(name = "patient_contacts", joinColumns = @JoinColumn(name = "patient_id"))
    private List<PatientContact> contacts;

    // Consent flags
    @Column(name = "consent_to_treatment")
    private Boolean consentToTreatment;

    @Column(name = "consent_to_data_sharing")
    private Boolean consentToDataSharing;

    // Metadata
    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "deleted")
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private java.time.Instant deletedAt;

    @Column(name = "deletion_reason")
    private String deletionReason;
    
    /**
     * Create cross-facility referral context.
     */
    public FacilityContext getReferralContext(String targetFacilityId) {
        return new FacilityContext(
            targetFacilityId,
            "Referred Facility", // Would be resolved from facility registry
            null,
            null,
            true
        );
    }
}
