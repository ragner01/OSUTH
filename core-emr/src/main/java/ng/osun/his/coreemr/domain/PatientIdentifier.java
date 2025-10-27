package ng.osun.his.coreemr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Patient identifier embedded class.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientIdentifier {
    
    @Column(name = "identifier_type")
    private String identifierType; // NIN, VOTER_ID, PASSPORT, etc.
    
    @Column(name = "identifier_value")
    private String identifierValue;
    
    @Column(name = "issuing_authority")
    private String issuingAuthority;
    
    @Column(name = "issue_date")
    private java.time.LocalDate issueDate;
    
    @Column(name = "expiry_date")
    private java.time.LocalDate expiryDate;
}

