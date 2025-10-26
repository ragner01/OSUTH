package ng.osun.his.billing.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Price book for services across departments.
 */
@Entity
@Table(name = "price_books", indexes = {
    @Index(name = "idx_price_service", columnList = "service_code"),
    @Index(name = "idx_price_dept", columnList = "department"),
    @Index(name = "idx_price_effective", columnList = "effective_date")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PriceBook extends BaseEntity {

    @Column(name = "service_code", nullable = false, length = 50)
    private String serviceCode; // LAB-CBC, CONSULT-GP, DRUG-PARACETAMOL

    @Column(name = "service_name", nullable = false, length = 200)
    private String serviceName;

    @Column(name = "department", nullable = false, length = 50)
    private String department; // LAB, RADIOLOGY, CONSULTATION, PHARMACY

    @Column(name = "service_category", length = 50)
    private String serviceCategory; // CONSULTATION, LABORATORY, RADIOLOGY, MEDICATION, PROCEDURE

    @Column(name = "unit_price_ngn", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPriceNGN;

    @Column(name = "tariff_code", length = 50)
    private String tariffCode; // NHIA tariff code

    @Column(name = "nhia_price_ngn", precision = 19, scale = 2)
    private BigDecimal nhiaPriceNGN;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "NGN";

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "requires_authorization")
    private Boolean requiresAuthorization = false;

    @Column(name = "description", length = 1000)
    private String description;
}

