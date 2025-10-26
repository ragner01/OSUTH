package ng.osun.his.platform.audit;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Immutable audit event entity for PHI-grade compliance.
 */
@Entity
@Table(name = "audit_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 36)
    private String id;

    @Column(name = "event_timestamp", nullable = false)
    private Instant eventTimestamp;

    @Column(name = "user_id", length = 100, nullable = false)
    private String userId;

    @Column(name = "user_role", length = 50)
    private String userRole;

    @Column(name = "action_type", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Column(name = "resource_type", length = 100)
    private String resourceType;

    @Column(name = "resource_id", length = 100)
    private String resourceId;

    @Column(name = "purpose_of_use", length = 100)
    private String purposeOfUse;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Lob
    @Column(name = "event_details", columnDefinition = "TEXT")
    private String eventDetails;

    @Column(name = "signed_hash", length = 512)
    private String signedHash;

    public enum ActionType {
        CREATE, READ, UPDATE, DELETE, SEARCH, EXPORT, LOGIN, LOGOUT
    }
}

