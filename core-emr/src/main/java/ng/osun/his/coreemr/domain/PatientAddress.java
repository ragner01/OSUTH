package ng.osun.his.coreemr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Patient address embedded class.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientAddress {
    
    @Column(name = "address_line")
    private String addressLine;
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "state")
    private String state;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    @Column(name = "country")
    private String country = "Nigeria";
    
    @Column(name = "address_type")
    private String addressType; // HOME, WORK, TEMPORARY
}

