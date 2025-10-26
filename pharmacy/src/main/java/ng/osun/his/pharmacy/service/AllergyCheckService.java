package ng.osun.his.pharmacy.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for checking drug-allergy interactions.
 */
@Service
@Slf4j
public class AllergyCheckService {

    // Stub allergy database - In production, use actual medication allergy database
    private static final Map<String, List<String>> ALLERGY_RULES = new HashMap<>();

    static {
        // Penicillin allergy
        ALLERGY_RULES.put("PENICILLIN", Arrays.asList(
            "AMOXICILLIN", "AMPICILLIN", "PENICILLIN_V", "PENICILLIN_G", 
            "AMOXICILLIN_CLAVULANATE", "CEPHALEXIN"
        ));

        // Sulfa allergy
        ALLERGY_RULES.put("SULFA", Arrays.asList(
            "SULFAMETHOXAZOLE", "TRIMETHOPRIM", "SULFADIAZINE"
        ));

        // Aspirin allergy
        ALLERGY_RULES.put("ASPIRIN", Arrays.asList(
            "IBUPROFEN", "NAPROXEN", "DICLOFENAC", "INDOMETHACIN"
        ));

        // Latex allergy
        ALLERGY_RULES.put("LATEX", Arrays.asList("BANDAGES", "GLOVES"));
    }

    /**
     * Check if prescription has drug-allergy conflicts.
     */
    public AllergyCheckResult checkAllergies(List<String> patientAllergies, List<String> prescribedMeds) {
        List<String> conflicts = new ArrayList<>();
        Map<String, String> conflictDetails = new HashMap<>();

        for (String allergy : patientAllergies) {
            List<String> contraindicatedMeds = ALLERGY_RULES.get(allergy.toUpperCase());

            if (contraindicatedMeds != null) {
                for (String prescribedMed : prescribedMeds) {
                    if (isContraindicated(prescribedMed, contraindicatedMeds)) {
                        conflicts.add(prescribedMed);
                        conflictDetails.put(prescribedMed, 
                            String.format("Contraindicated with allergy to %s", allergy));
                    }
                }
            }
        }

        return new AllergyCheckResult(
            conflicts.isEmpty(),
            conflicts,
            conflictDetails
        );
    }

    private boolean isContraindicated(String medication, List<String> contraindicatedMeds) {
        String medUpper = medication.toUpperCase();
        return contraindicatedMeds.stream()
            .anyMatch(contra -> medUpper.contains(contra) || contra.contains(medUpper));
    }

    public static class AllergyCheckResult {
        private final boolean safe;
        private final List<String> conflicts;
        private final Map<String, String> conflictDetails;

        public AllergyCheckResult(boolean safe, List<String> conflicts, Map<String, String> conflictDetails) {
            this.safe = safe;
            this.conflicts = conflicts;
            this.conflictDetails = conflictDetails;
        }

        public boolean isSafe() {
            return safe;
        }

        public List<String> getConflicts() {
            return conflicts;
        }

        public Map<String, String> getConflictDetails() {
            return conflictDetails;
        }
    }
}

