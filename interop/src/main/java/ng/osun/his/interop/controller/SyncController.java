package ng.osun.his.interop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

/**
 * Delta-based sync API for offline support.
 */
@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
@Slf4j
public class SyncController {

    /**
     * Get changes since timestamp for sync.
     */
    @GetMapping("/changes")
    public ResponseEntity<?> getChanges(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant since,
            @RequestParam String module, // core-emr, appointments, orders, pharmacy, billing
            @RequestParam(required = false) Integer limit,
            Authentication auth) {
        
        log.info("Sync changes for module {} since {} by user {}", module, since, auth.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("module", module);
        response.put("since", since.toString());
        
        // Get changes based on module
        List<Map<String, Object>> changes = getChangesForModule(module, since, limit != null ? limit : 100);
        
        response.put("changes", changes);
        response.put("count", changes.size());
        
        // Add tombstone support
        List<Map<String, Object>> tombstones = getTombstones(module, since);
        response.put("tombstones", tombstones);
        
        // Calculate sync cursor for next request
        String syncCursor = generateSyncCursor(changes);
        
        return ResponseEntity.ok()
            .header("X-Sync-Cursor", syncCursor)
            .header("X-Change-Count", String.valueOf(changes.size()))
            .header("X-Tombstone-Count", String.valueOf(tombstones.size()))
            .body(response);
    }

    /**
     * Get sync status for all modules.
     */
    @GetMapping("/status")
    public ResponseEntity<?> getSyncStatus(Authentication auth) {
        log.info("Sync status requested by user {}", auth.getName());

        Map<String, Object> status = new HashMap<>();
        status.put("timestamp", Instant.now().toString());
        status.put("modules", Map.of(
            "core-emr", Map.of("available", true, "lastSync", "2024-01-01T00:00:00Z"),
            "appointments", Map.of("available", true, "lastSync", "2024-01-01T00:00:00Z"),
            "orders-lab-rad", Map.of("available", true, "lastSync", "2024-01-01T00:00:00Z"),
            "pharmacy", Map.of("available", true, "lastSync", "2024-01-01T00:00:00Z"),
            "billing", Map.of("available", true, "lastSync", "2024-01-01T00:00:00Z")
        ));

        return ResponseEntity.ok(status);
    }

    /**
     * Acknowledge sync completion.
     */
    @PostMapping("/acknowledge")
    public ResponseEntity<?> acknowledgeSync(
            @RequestParam String syncCursor,
            @RequestParam String module,
            Authentication auth) {
        
        log.info("Sync acknowledged for module {} with cursor {} by user {}", module, syncCursor, auth.getName());
        
        return ResponseEntity.ok(Map.of(
            "status", "acknowledged",
            "timestamp", Instant.now().toString()
        ));
    }

    private List<Map<String, Object>> getChangesForModule(String module, Instant since, Integer limit) {
        // Stub implementation - would query module-specific repositories
        List<Map<String, Object>> changes = new ArrayList<>();
        
        // Add version vector information
        Map<String, Object> sampleChange = new HashMap<>();
        sampleChange.put("id", "123");
        sampleChange.put("type", "CREATE");
        sampleChange.put("entity", module);
        sampleChange.put("updatedAt", Instant.now().toString());
        sampleChange.put("version", 1);
        sampleChange.put("data", Map.of("field", "value"));
        
        changes.add(sampleChange);
        
        return changes;
    }

    private List<Map<String, Object>> getTombstones(String module, Instant since) {
        // Return IDs of deleted entities since timestamp
        List<Map<String, Object>> tombstones = new ArrayList<>();
        
        // Sample tombstone
        if (module.equals("core-emr")) {
            tombstones.add(Map.of(
                "id", "deleted-123",
                "deletedAt", since.minusSeconds(3600).toString(),
                "version", -1
            ));
        }
        
        return tombstones;
    }

    private String generateSyncCursor(List<Map<String, Object>> changes) {
        if (changes.isEmpty()) {
            return "empty";
        }
        
        Map<String, Object> lastChange = changes.get(changes.size() - 1);
        return Base64.getEncoder().encodeToString(
            (lastChange.get("id") + ":" + lastChange.get("updatedAt")).getBytes()
        );
    }
}

