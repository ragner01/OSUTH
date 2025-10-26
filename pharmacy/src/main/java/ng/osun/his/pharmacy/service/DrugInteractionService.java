package ng.osun.his.pharmacy.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for checking drug-drug interactions.
 */
@Service
@Slf4j
public class DrugInteractionService {

    // Stub interaction database - In production, use actual drug interaction database
    private static final Map<String, List<String>> INTERACTION_RULES = new HashMap<>();

    static {
        // Warfarin interactions
        INTERACTION_RULES.put("WARFARIN", Arrays.asList(
            "ASPIRIN", "IBUPROFEN", "FLUOXETINE", "PHENYTOIN", "RIFAMPIN"
        ));

        // ACE inhibitors with potassium-sparing diuretics
        INTERACTION_RULES.put("LISINOPRIL", Arrays.asList("SPIRONOLACTONE", "TRIAMTERENE"));
        INTERACTION_RULES.put("ENALAPRIL", Arrays.asList("SPIRONOLACTONE", "TRIAMTERENE"));

        // Digoxin with diuretics
        INTERACTION_RULES.put("DIGOXIN", Arrays.asList("FUROSEMIDE", "HYDROCHLOROTHIAZIDE"));

        // Statins with macrolide antibiotics
        INTERACTION_RULES.put("ATORVASTATIN", Arrays.asList("CLARITHROMYCIN", "ERYTHROMYCIN"));

        // MAOI interactions
        INTERACTION_RULES.put("PHENELZINE", Arrays.asList(
            "AMITRIPTYLINE", "TYRAMINE_RICH_FOODS", "DEXTROMETHORPHAN"
        ));
    }

    /**
     * Check if prescribed medications have drug-drug interactions.
     */
    public InteractionCheckResult checkInteractions(List<String> prescribedMeds) {
        List<String[]> conflicts = new ArrayList<>();
        Map<String, String> conflictDetails = new HashMap<>();

        // Check each pair of medications
        for (int i = 0; i < prescribedMeds.size(); i++) {
            for (int j = i + 1; j < prescribedMeds.size(); j++) {
                String med1 = prescribedMeds.get(i);
                String med2 = prescribedMeds.get(j);

                String interaction = findInteraction(med1, med2);
                if (interaction != null) {
                    conflicts.add(new String[]{med1, med2});
                    conflictDetails.put(med1 + " + " + med2, interaction);
                }
            }
        }

        return new InteractionCheckResult(
            conflicts.isEmpty(),
            conflicts,
            conflictDetails
        );
    }

    private String findInteraction(String med1, String med2) {
        List<String> interactions1 = INTERACTION_RULES.get(med1.toUpperCase());
        List<String> interactions2 = INTERACTION_RULES.get(med2.toUpperCase());

        if (interactions1 != null && interactions1.contains(med2.toUpperCase())) {
            return String.format("May interact with %s", med2);
        }

        if (interactions2 != null && interactions2.contains(med1.toUpperCase())) {
            return String.format("May interact with %s", med1);
        }

        return null;
    }

    public static class InteractionCheckResult {
        private final boolean safe;
        private final List<String[]> conflicts;
        private final Map<String, String> conflictDetails;

        public InteractionCheckResult(boolean safe, List<String[]> conflicts, Map<String, String> conflictDetails) {
            this.safe = safe;
            this.conflicts = conflicts;
            this.conflictDetails = conflictDetails;
        }

        public boolean isSafe() {
            return safe;
        }

        public List<String[]> getConflicts() {
            return conflicts;
        }

        public Map<String, String> getConflictDetails() {
            return conflictDetails;
        }
    }
}

