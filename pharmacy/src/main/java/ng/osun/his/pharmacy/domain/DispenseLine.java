package ng.osun.his.pharmacy.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispenseLine {
    @Column(name = "medication_code")
    private String medicationCode;

    @Column(name = "medication_name")
    private String medicationName;

    @Column(name = "quantity_dispensed")
    private Integer quantityDispensed;

    @Column(name = "batch_id")
    private String batchId;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "line_total")
    private BigDecimal lineTotal;

    @Column(name = "expiry_days")
    private Integer expiryDays; // Days until expiry
}

