package ng.osun.his.appointments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ng.osun.his.appointments.domain.TriageScore;
import org.springframework.stereotype.Service;

/**
 * Triage scoring service implementing NEWS2-like logic.
 * Computes risk band and queue priority.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TriageService {

    /**
     * Calculate triage score based on NEWS2-like criteria.
     */
    public TriageScore calculateTriageScore(TriageScore triage) {
        int score = 0;
        
        // Temperature
        if (triage.getTemperature() != null) {
            double temp = triage.getTemperature();
            if (temp >= 36.1 && temp <= 38.0) {
                score += 0;
            } else if (temp >= 35.1 && temp <= 36.0) {
                score += 1;
            } else if (temp >= 38.1 && temp <= 39.0 || temp >= 35.0) {
                score += 1;
            } else if (temp >= 39.1) {
                score += 2;
            }
        }

        // Heart Rate
        if (triage.getHeartRate() != null) {
            int hr = triage.getHeartRate();
            if (hr >= 51 && hr <= 90) {
                score += 0;
            } else if (hr >= 41 && hr <= 50 || hr >= 91 && hr <= 110) {
                score += 1;
            } else if (hr >= 111 && hr <= 130) {
                score += 1;
            } else if (hr >= 131 || hr <= 40) {
                score += 3;
            }
        }

        // Respiratory Rate
        if (triage.getRespiratoryRate() != null) {
            int rr = triage.getRespiratoryRate();
            if (rr >= 9 && rr <= 11) {
                score += 1;
            } else if (rr >= 12 && rr <= 20) {
                score += 0;
            } else if (rr >= 21 && rr <= 24) {
                score += 2;
            } else if (rr >= 25) {
                score += 3;
            }
        }

        // Oxygen Saturation
        if (triage.getOxygenSaturation() != null) {
            int spo2 = triage.getOxygenSaturation();
            if (spo2 >= 96) {
                score += 0;
            } else if (spo2 >= 94 && spo2 <= 95) {
                score += 1;
            } else if (spo2 >= 92 && spo2 <= 93) {
                score += 2;
            } else if (spo2 <= 91) {
                score += 3;
            }
        }

        // Consciousness level
        if (triage.getConsciousnessLevel() != null) {
            String level = triage.getConsciousnessLevel();
            if ("ALERT".equals(level)) {
                score += 0;
            } else if ("RESPONDS_TO_VOICE".equals(level)) {
                score += 3;
            } else if ("RESPONDS_TO_PAIN".equals(level)) {
                score += 3;
            } else if ("UNRESPONSIVE".equals(level)) {
                score += 3;
            }
        }

        // Pain score (0-10 scale)
        if (triage.getPainScore() != null) {
            if (triage.getPainScore() >= 7) {
                score += 2;
            } else if (triage.getPainScore() >= 4) {
                score += 1;
            }
        }

        triage.setCalculatedScore(score);

        // Determine priority level
        if (score >= 7) {
            triage.setPriorityLevel("CRITICAL");
        } else if (score >= 4) {
            triage.setPriorityLevel("HIGH");
        } else if (score >= 2) {
            triage.setPriorityLevel("MEDIUM");
        } else {
            triage.setPriorityLevel("LOW");
        }

        triage.setAssessedAt(java.time.Instant.now());

        log.info("Calculated triage score: {} for appointment: {} with priority: {}", 
            score, triage.getAppointmentId(), triage.getPriorityLevel());

        return triage;
    }

    /**
     * Determine urgency based on priority level.
     */
    public int getQueuePriority(String priorityLevel) {
        switch (priorityLevel) {
            case "CRITICAL": return 1;
            case "HIGH": return 2;
            case "MEDIUM": return 3;
            case "LOW": return 4;
            default: return 5;
        }
    }
}

