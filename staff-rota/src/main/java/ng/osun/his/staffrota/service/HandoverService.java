package ng.osun.his.staffrota.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ng.osun.his.staffrota.domain.HandoverChecklist;
import ng.osun.his.staffrota.domain.HandoverItem;
import ng.osun.his.staffrota.repository.HandoverChecklistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.UUID;

/**
 * Service for safe patient handover.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HandoverService {

    private final HandoverChecklistRepository handoverChecklistRepository;

    /**
     * Create handover checklist for discharge.
     */
    @Transactional
    public HandoverChecklist createDischargeHandover(String patientId, String encounterId, String fromStaffId, String toStaffId) {
        log.info("Creating discharge handover for patient {}", patientId);

        HandoverChecklist checklist = new HandoverChecklist();
        checklist.setId(UUID.randomUUID().toString());
        checklist.setPatientId(patientId);
        checklist.setEncounterId(encounterId);
        checklist.setHandoverType("DISCHARGE");
        checklist.setFromStaffId(fromStaffId);
        checklist.setToStaffId(toStaffId);
        checklist.setStatus("IN_PROGRESS");

        // Create standard discharge checklist items
        checklist.setItems(Arrays.asList(
            createChecklistItem("VITALS", "All vital signs documented", false),
            createChecklistItem("MEDICATIONS", "Discharge medications prescribed", false),
            createChecklistItem("ALLERGIES", "Allergies documented and communicated", false),
            createChecklistItem("DIAGNOSIS", "Primary and secondary diagnoses confirmed", false),
            createChecklistItem("FOLLOW_UP", "Follow-up appointments scheduled", false),
            createChecklistItem("INSTRUCTIONS", "Discharge instructions provided to patient", false)
        ));

        HandoverChecklist saved = handoverChecklistRepository.save(checklist);
        log.info("Discharge handover created: {}", saved.getId());
        return saved;
    }

    /**
     * Complete checklist item.
     */
    @Transactional
    public void completeChecklistItem(String checklistId, int itemIndex, String completedBy) {
        HandoverChecklist checklist = handoverChecklistRepository.findById(checklistId)
            .orElseThrow(() -> new IllegalArgumentException("Checklist not found"));

        if (itemIndex >= 0 && itemIndex < checklist.getItems().size()) {
            HandoverItem item = checklist.getItems().get(itemIndex);
            item.setCompleted(true);
            item.setCompletedAt(java.time.Instant.now());
            item.setCompletedBy(completedBy);

            handoverChecklistRepository.save(checklist);
            log.info("Checklist item {} completed for checklist {}", itemIndex, checklistId);

            // Auto-complete if all items done
            boolean allComplete = checklist.getItems().stream().allMatch(HandoverItem::getCompleted);
            if (allComplete) {
                checklist.setStatus("COMPLETED");
                checklist.setCompletedAt(java.time.Instant.now());
                handoverChecklistRepository.save(checklist);
            }
        }
    }

    /**
     * Block discharge if checklist incomplete.
     */
    public boolean canDischarge(String patientId) {
        return handoverChecklistRepository.findByPatientIdAndStatus(patientId, "COMPLETED")
            .isPresent();
    }

    private HandoverItem createChecklistItem(String category, String description, boolean completed) {
        HandoverItem item = new HandoverItem();
        item.setItemCategory(category);
        item.setItemDescription(description);
        item.setCompleted(completed);
        return item;
    }
}

