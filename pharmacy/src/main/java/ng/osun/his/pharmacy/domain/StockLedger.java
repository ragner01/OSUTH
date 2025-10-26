package ng.osun.his.pharmacy.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Stock ledger for inventory tracking with FEFO support.
 */
@Entity
@Table(name = "stock_ledger", indexes = {
    @Index(name = "idx_stock_medication", columnList = "medication_code"),
    @Index(name = "idx_stock_batch", columnList = "batch_number"),
    @Index(name = "idx_stock_expiry", columnList = "expiry_date"),
    @Index(name = "idx_stock_status", columnList = "status")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class StockLedger extends BaseEntity {

    @Column(name = "medication_code", nullable = false, length = 50)
    private String medicationCode;

    @Column(name = "batch_number", nullable = false, length = 100)
    private String batchNumber;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_cost_ngn", precision = 10, scale = 2)
    private BigDecimal unitCostNGN;

    @Column(name = "movement_type", length = 50)
    private String movementType; // RECEIPT, DISPENSE, ADJUSTMENT, RETURN, EXPIRED

    @Column(name = "reference_id", length = 36)
    private String referenceId; // Dispense ID, Receipt ID, etc.

    @Column(name = "status", length = 50)
    private String status; // AVAILABLE, RESERVED, EXPIRED, DAMAGED

    @Column(name = "location", length = 100)
    private String location; // Ward, Pharmacy, Store, etc.

    @Column(name = "supplier_id", length = 36)
    private String supplierId;

    @Column(name = "last_count_date")
    private LocalDate lastCountDate;
}

