package ng.osun.his.billing.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

/**
 * Payer entity (NHIA, HMOs, Self-pay).
 */
@Entity
@Table(name = "payers")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Payer extends BaseEntity {

    @Column(name = "payer_code", unique = true, nullable = false, length = 50)
    private String payerCode;

    @Column(name = "payer_name", nullable = false, length = 200)
    private String payerName;

    @Column(name = "payer_type", nullable = false, length = 50)
    private String payerType; // NHIA, HMO, SELF_PAY

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "api_endpoint", length = 500)
    private String apiEndpoint;

    @Column(name = "api_key", length = 500)
    private String apiKey;

    @Column(name = "settlement_terms_days")
    private Integer settlementTermsDays = 30;

    @Column(name = "credit_limit_ngn", precision = 19, scale = 2)
    private java.math.BigDecimal creditLimitNGN;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}

