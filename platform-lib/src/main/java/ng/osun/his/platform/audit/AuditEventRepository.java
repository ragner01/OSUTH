package ng.osun.his.platform.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository for audit events.
 */
@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, String> {
    List<AuditEvent> findByUserIdAndEventTimestampBetween(String userId, Instant start, Instant end);
    List<AuditEvent> findByResourceTypeAndResourceId(String resourceType, String resourceId);
}

