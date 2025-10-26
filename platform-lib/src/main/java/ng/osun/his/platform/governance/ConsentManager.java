package ng.osun.his.platform.governance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Consent management for purpose-of-use tracking.
 */
@Component
@Slf4j
public class ConsentManager {

    private final Map<String, PatientConsent> consents = new ConcurrentHashMap<>();

    /**
     * Check if patient has consented for specific purpose.
     */
    public boolean hasConsent(String patientId, String purpose) {
        PatientConsent consent = consents.get(patientId);
        if (consent == null) {
            log.warn("No consent record found for patient {}", patientId);
            return false;
        }
        
        return consent.isAllowed(purpose);
    }

    /**
     * Record consent decision.
     */
    public void recordConsent(String patientId, String purpose, boolean allowed, String grantedBy) {
        PatientConsent consent = consents.computeIfAbsent(
            patientId, 
            k -> new PatientConsent(patientId)
        );
        consent.setPurpose(purpose, allowed);
        consent.setLastUpdatedBy(grantedBy);
        
        log.info("Consent {} for patient {} - purpose: {}", 
            allowed ? "granted" : "denied", patientId, purpose);
    }

    static class PatientConsent {
        private final String patientId;
        private final Map<String, Boolean> purposes = new ConcurrentHashMap<>();
        private String lastUpdatedBy;

        PatientConsent(String patientId) {
            this.patientId = patientId;
        }

        boolean isAllowed(String purpose) {
            return purposes.getOrDefault(purpose, false);
        }

        void setPurpose(String purpose, boolean allowed) {
            purposes.put(purpose, allowed);
        }

        void setLastUpdatedBy(String user) {
            this.lastUpdatedBy = user;
        }
    }
}

