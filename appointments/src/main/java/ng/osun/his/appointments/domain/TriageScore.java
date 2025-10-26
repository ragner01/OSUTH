package ng.osun.his.appointments.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

/**
 * Triage scoring entity (NEWS2-like).
 */
@Entity
@Table(name = "triage_scores", indexes = {
    @Index(name = "idx_triage_appointment", columnList = "appointment_id"),
    @Index(name = "idx_triage_priority", columnList = "priority_level")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TriageScore extends BaseEntity {

    @Column(name = "appointment_id", nullable = false, length = 36)
    private String appointmentId;

    @Column(name = "patient_id", nullable = false, length = 36)
    private String patientId;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "systolic_bp")
    private Integer systolicBp;

    @Column(name = "diastolic_bp")
    private Integer diastolicBp;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "respiratory_rate")
    private Integer respiratoryRate;

    @Column(name = "oxygen_saturation")
    private Integer oxygenSaturation;

    @Column(name = "consciousness_level", length = 50)
    private String consciousnessLevel; // ALERT, RESPONDS_TO_VOICE, RESPONDS_TO_PAIN, UNRESPONSIVE

    @Column(name = "pain_score")
    private Integer painScore; // 0-10 scale

    @Column(name = "pregnancy_flag")
    private Boolean pregnancyFlag = false;

    @Column(name = "malaria_risk")
    private Boolean malariaRisk = false;

    @Column(name = "other_symptoms", length = 500)
    private String otherSymptoms;

    @Column(name = "calculated_score")
    private Integer calculatedScore; // NEWS2 score

    @Column(name = "priority_level", length = 50)
    private String priorityLevel; // CRITICAL, HIGH, MEDIUM, LOW

    @Column(name = "assessed_by", length = 100)
    private String assessedBy;

    @Column(name = "assessed_at", nullable = false)
    private java.time.Instant assessedAt;

    // Lombok @Data should generate these, but adding manually for Java 11 compatibility
    public Integer getPainScore() {
        return painScore;
    }

    public void setPainScore(Integer painScore) {
        this.painScore = painScore;
    }

    public Integer getCalculatedScore() {
        return calculatedScore;
    }

    public void setCalculatedScore(Integer calculatedScore) {
        this.calculatedScore = calculatedScore;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }
}

