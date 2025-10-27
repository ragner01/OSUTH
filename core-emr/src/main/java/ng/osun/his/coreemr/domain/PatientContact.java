package ng.osun.his.coreemr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Patient contact embedded class.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientContact {
    
    @Column(name = "contact_name")
    private String contactName;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @Column(name = "contact_email")
    private String contactEmail;
    
    @Column(name = "contact_relationship")
    private String contactRelationship; // SPOUSE, PARENT, CHILD, FRIEND, etc.
    
    @Column(name = "is_primary_contact")
    private Boolean isPrimaryContact = false;
}

