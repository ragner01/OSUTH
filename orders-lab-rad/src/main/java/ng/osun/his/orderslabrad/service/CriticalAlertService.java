package ng.osun.his.orderslabrad.service;

import lombok.extern.slf4j.Slf4j;
import ng.osun.his.orderslabrad.domain.Result;
import org.springframework.stereotype.Service;

/**
 * Service for sending critical value alerts.
 */
@Service
@Slf4j
public class CriticalAlertService {

    /**
     * Send critical alert to clinician.
     * In production: integrate with SMS gateway and notification service.
     */
    public void sendCriticalAlert(Result result, String reason) {
        // TODO: Integrate with SMS gateway (e.g., Twilio)
        // TODO: Send to notification service for in-app alerts

        String smsMessage = String.format(
            "CRITICAL: Patient %s - Test: %s. Value: %s. Reason: %s. Check system immediately.",
            maskPatientId(result.getPatientId()),
            result.getTestName(),
            result.getValueText() != null ? result.getValueText() : result.getValueNumeric(),
            reason
        );

        log.error("CRITICAL ALERT - SMS would be sent: {}", smsMessage);
        log.error("CRITICAL ALERT - In-app notification triggered for result {}", result.getResultNumber());

        // In production:
        // 1. Send SMS to ordering clinician
        // 2. Create in-app notification
        // 3. Log to notification service
        // 4. Send to Kafka for external systems
    }

    /**
     * Mask patient ID for non-PHI SMS content.
     */
    private String maskPatientId(String patientId) {
        if (patientId == null || patientId.length() < 4) {
            return "****";
        }
        return patientId.substring(0, 4) + "****";
    }
}

