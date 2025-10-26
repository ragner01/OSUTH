package ng.osun.his.pharmacy.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * Dispense entity with FEFO batch management.
 */
@Entity
@Table(name = "dispenses", indexes = {
    @Index(name = "idx_dispenses_prescription", columnList = "prescription_id"),
    @Index(name = "idx_dispenses_patient", columnList = "patient_id"),
    @Index(name = "idx_dispenses_date", columnList = "dispense_date")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Dispense extends BaseEntity {

    @Column(name = "dispense_number", unique = true, length = 50)
    private String dispenseNumber;

    @Column(name = "prescription_id", nullable = false, length = 36)
    private String prescriptionId;

    @Column(name = "patient_id", nullable = false, length = 36)
    private String patientId;

    @ElementCollection
    @CollectionTable(name = "dispense_lines", joinColumns = @JoinColumn(name = "dispense_id"))
    private List<DispenseLine> lines;

    @Column(name = "dispense_date", nullable = false)
    private java.time.Instant dispenseDate;

    @Column(name = "dispensed_by", nullable = false, length = 100)
    private String dispensedBy;

    @Column(name = "dispensed_by_name", length = 200)
    private String dispensedByName;

    @Column(name = "total_amount_ngn", precision = 10, scale = 2)
    private BigDecimal totalAmountNGN;

    @Column(name = "payment_status", length = 50)
    private String paymentStatus; // PAID, PENDING, WAIVED

    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    @Column(name = "label_text", length = 2000)
    private String labelText; // Drug labeling for patient

    @Column(name = "instruction_sheet", columnDefinition = "TEXT")
    private String instructionSheet;
}

