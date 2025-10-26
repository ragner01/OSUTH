package ng.osun.his.orderslabrad.service;

import lombok.extern.slf4j.Slf4j;
import ng.osun.his.orderslabrad.domain.Result;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for reflex testing based on result values.
 * Implements simple rules for automatic follow-up testing.
 */
@Service
@Slf4j
public class ReflexTestingService {

    /**
     * Check if reflex testing is needed based on result.
     * 
     * @param result Current result
     * @return List of suggested reflex tests
     */
    public List<ReflexTest> suggestReflexTests(Result result) {
        List<ReflexTest> reflexTests = new ArrayList<>();

        String testCode = result.getTestCode();
        Double value = result.getValueNumeric() != null ? result.getValueNumeric().doubleValue() : null;

        // Example rules:
        
        // Rule 1: If glucose is high, suggest HbA1c
        if ("GLUCOSE".equals(testCode) && value != null && value > 126) {
            reflexTests.add(new ReflexTest("HbA1c", "Check for diabetes", "ROUTINE"));
        }

        // Rule 2: If cholesterol is high, suggest lipid panel
        if ("CHOLESTEROL".equals(testCode) && value != null && value > 240) {
            reflexTests.add(new ReflexTest("LIPID_PANEL", "Full lipid assessment", "ROUTINE"));
        }

        // Rule 3: If TSH is abnormal, suggest Free T4
        if ("TSH".equals(testCode) && value != null && (value < 0.5 || value > 5.0)) {
            reflexTests.add(new ReflexTest("Free_T4", "Evaluate thyroid function", "ROUTINE"));
        }

        // Rule 4: If PSA is elevated, suggest confirmation
        if ("PSA".equals(testCode) && value != null && value > 4.0) {
            reflexTests.add(new ReflexTest("PSA_FREE", "Confirm elevated PSA", "URGENT"));
        }

        if (!reflexTests.isEmpty()) {
            log.info("Suggested {} reflex tests for result {}", reflexTests.size(), result.getResultNumber());
        }

        return reflexTests;
    }

    public static class ReflexTest {
        private final String testCode;
        private final String reason;
        private final String priority;

        public ReflexTest(String testCode, String reason, String priority) {
            this.testCode = testCode;
            this.reason = reason;
            this.priority = priority;
        }

        public String getTestCode() {
            return testCode;
        }

        public String getReason() {
            return reason;
        }

        public String getPriority() {
            return priority;
        }
    }
}

