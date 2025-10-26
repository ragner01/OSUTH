package ng.osun.his.orderslabrad.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * Result entity for lab/radiology test results.
 */
@Entity
@Table(name = "results", indexes = {
    @Index(name = "idx_results_order_item", columnList = "order_item_id"),
    @Index(name = "idx_results_patient", columnList = "patient_id"),
    @Index(name = "idx_results_abnormal", columnList = "abnormal_flag"),
    @Index(name = "idx_results_critical", columnList = "critical_flag")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Result extends BaseEntity {

    @Column(name = "result_number", unique = true, length = 50)
    private String resultNumber;

    @Column(name = "order_id", nullable = false, length = 36)
    private String orderId;

    @Column(name = "order_item_id", nullable = false, length = 36)
    private String orderItemId;

    @Column(name = "patient_id", nullable = false, length = 36)
    private String patientId;

    @Column(name = "test_code", nullable = false, length = 50)
    private String testCode; // LOINC

    @Column(name = "test_name", nullable = false, length = 200)
    private String testName;

    @Column(name = "value_numeric", precision = 10, scale = 2)
    private BigDecimal valueNumeric;

    @Column(name = "value_text", length = 1000)
    private String valueText;

    @Column(name = "value_coded", length = 100)
    private String valueCoded; // Code value

    @Column(name = "value_type", length = 20)
    private String valueType; // NUMERIC, TEXT, CODED

    @Column(name = "units", length = 50)
    private String units;

    @Column(name = "reference_range", length = 100)
    private String referenceRange;

    @Column(name = "abnormal_flag")
    private Boolean abnormalFlag = false;

    @Column(name = "abnormal_flag_text", length = 10)
    private String abnormalFlagText; // H, L, >, <

    @Column(name = "critical_flag")
    private Boolean criticalFlag = false;

    @Column(name = "critical_value")
    private Boolean criticalValue = false;

    @Column(name = "verified")
    private Boolean verified = false;

    @Column(name = "verified_by", length = 100)
    private String verifiedBy;

    @Column(name = "verified_by_name", length = 200)
    private String verifiedByName;

    @Column(name = "verified_at")
    private java.time.Instant verifiedAt;

    @Column(name = "performed_at", nullable = false)
    private java.time.Instant performedAt;

    @Column(name = "performed_by", length = 100)
    private String performedBy;

    @Column(name = "device_id", length = 100)
    private String deviceId;

    @Column(name = "comments", length = 1000)
    private String comments;
}

