package ng.osun.his.orderslabrad.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ng.osun.his.orderslabrad.domain.Result;
import ng.osun.his.orderslabrad.repository.ResultRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for detecting critical values and triggering alerts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CriticalValueService {

    private final ResultRepository resultRepository;
    private final CriticalAlertService criticalAlertService;

    // Critical value ranges (simplified example)
    private static final Map<String, CriticalRange> CRITICAL_RANGES = new HashMap<>();

    static {
        CRITICAL_RANGES.put("GLUCOSE", new CriticalRange(250.0, 40.0));
        CRITICAL_RANGES.put("POTASSIUM", new CriticalRange(6.5, 2.5));
        CRITICAL_RANGES.put("SODIUM", new CriticalRange(160.0, 120.0));
        CRITICAL_RANGES.put("CREATININE", new CriticalRange(5.0, null));
        CRITICAL_RANGES.put("HEMOGLOBIN", new CriticalRange(null, 7.0));
        CRITICAL_RANGES.put("PLATELET", new CriticalRange(null, 50.0));
        CRITICAL_RANGES.put("BLOOD_CULTURE", new CriticalRange(null, null)); // Always critical
    }

    /**
     * Check if result value is critical and trigger alert.
     */
    public void checkCriticalValue(Result result) {
        String testCode = result.getTestCode();
        CriticalRange range = CRITICAL_RANGES.get(testCode);

        if (range == null) {
            return; // No critical range defined
        }

        boolean isCritical = false;
        String reason = null;

        if (result.getValueNumeric() != null) {
            Double value = result.getValueNumeric().doubleValue();

            if (range.getMax() != null && value > range.getMax()) {
                isCritical = true;
                reason = String.format("Critical high: %s = %.2f (normal max: %.2f)", testCode, value, range.getMax());
            } else if (range.getMin() != null && value < range.getMin()) {
                isCritical = true;
                reason = String.format("Critical low: %s = %.2f (normal min: %.2f)", testCode, value, range.getMin());
            }
        } else if (result.getValueText() != null) {
            // For text values like "POSITIVE" for cultures
            if (testCode.contains("CULTURE") && result.getValueText().toUpperCase().contains("POSITIVE")) {
                isCritical = true;
                reason = "Critical positive culture: " + result.getValueText();
            }
        }

        if (isCritical) {
            result.setCriticalFlag(true);
            result.setCriticalValue(true);
            resultRepository.save(result);

            log.warn("CRITICAL VALUE DETECTED: {}", reason);

            // Trigger critical alert
            criticalAlertService.sendCriticalAlert(result, reason);
        }
    }

    private static class CriticalRange {
        private final Double max;
        private final Double min;

        public CriticalRange(Double max, Double min) {
            this.max = max;
            this.min = min;
        }

        public Double getMax() {
            return max;
        }

        public Double getMin() {
            return min;
        }
    }
}

