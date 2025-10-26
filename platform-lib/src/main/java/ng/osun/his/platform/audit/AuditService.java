package ng.osun.his.platform.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/**
 * Service for immutably logging audit events to DB and Kafka.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private static final String AUDIT_TOPIC = "audit.events";

    private final AuditEventRepository auditEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Log an audit event to both database and Kafka for tamper-evidence.
     */
    @Transactional
    public void logEvent(AuditEvent event) {
        try {
            // Generate signed hash for tamper-evidence
            event.setSignedHash(computeHash(event));
            event.setEventTimestamp(Instant.now());

            // Save to database
            auditEventRepository.save(event);

            // Publish to Kafka for external audit trail
            kafkaTemplate.send(AUDIT_TOPIC, event.getId(), event.toString());

            log.debug("Audit event logged: {}", event.getId());
        } catch (Exception e) {
            log.error("Failed to log audit event", e);
            // Don't throw - audit failure should not block business logic
        }
    }

    private String computeHash(AuditEvent event) {
        try {
            String data = event.getUserId() + "|" + event.getActionType() + "|" + event.getResourceType() + "|" + event.getResourceId();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            log.error("Error computing hash", e);
            return "ERROR";
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

