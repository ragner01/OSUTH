package ng.osun.his.platform.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Facility context for multi-facility support.
 * All entities should include facilityId for data isolation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityContext {
    
    /**
     * Unique facility identifier.
     * Examples: FAC001 (Osun Teaching Hospital), FAC002 (Ejigbo PHC)
     */
    private String facilityId;
    
    /**
     * Facility name for display.
     */
    private String facilityName;
    
    /**
     * Local Government Area (LGA).
     */
    private String lga;
    
    /**
     * Facility type (PRIMARY/SECONDARY/TERTIARY/SPECIALIST).
     */
    private String facilityType;
    
    /**
     * Whether this facility is active.
     */
    private Boolean active;
    
    /**
     * Check if facility is valid.
     */
    public boolean isValid() {
        return facilityId != null && 
               !facilityId.trim().isEmpty() && 
               facilityName != null &&
               !facilityName.trim().isEmpty();
    }
    
    /**
     * Get display name.
     */
    public String getDisplayName() {
        return String.format("%s (%s)", facilityName, facilityId);
    }
}

