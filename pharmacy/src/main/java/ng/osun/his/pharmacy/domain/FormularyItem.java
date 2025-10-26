package ng.osun.his.pharmacy.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * Formulary item with DDD (Defined Daily Dose) and pricing.
 */
@Entity
@Table(name = "formulary_items", indexes = {
    @Index(name = "idx_formulary_medication", columnList = "medication_id"),
    @Index(name = "idx_formulary_active", columnList = "active")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class FormularyItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    @Column(name = "ddd", precision = 10, scale = 2)
    private BigDecimal ddd; // Defined Daily Dose

    @Column(name = "ddd_unit", length = 50)
    private String dddUnit;

    @Column(name = "strength", length = 100)
    private String strength;

    @Column(name = "route", length = 100)
    private String route;

    @Column(name = "unit_price_ngn", precision = 10, scale = 2)
    private BigDecimal unitPriceNGN;

    @Column(name = "reimbursable")
    private Boolean reimbursable = false;

    @Column(name = "requires_prescription")
    private Boolean requiresPrescription = true;

    @Column(name = "schedule", length = 50)
    private String schedule; // Schedule II, III, etc.

    @Column(name = "active")
    private Boolean active = true;
}

