package ng.osun.his.pharmacy.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

/**
 * Medication master data entity.
 */
@Entity
@Table(name = "medications", indexes = {
    @Index(name = "idx_medications_code", columnList = "code"),
    @Index(name = "idx_medications_name", columnList = "name")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Medication extends BaseEntity {

    @Column(name = "code", unique = true, length = 50)
    private String code; // NAFDAC number or internal code

    @Column(name = "name", nullable = false, length = 200)
    private String name; // Generic name

    @Column(name = "brand_name", length = 200)
    private String brandName;

    @Column(name = "dosage_form", length = 100)
    private String dosageForm; // Tablet, Capsule, Syrup, Injection, etc.

    @Column(name = "strength", length = 100)
    private String strength; // 500mg, 10% solution, etc.

    @Column(name = "route", length = 100)
    private String route; // Oral, IV, IM, Topical, etc.

    @Column(name = "therapeutic_class", length = 200)
    private String therapeuticClass; // Antibiotic, Antihypertensive, etc.

    @Column(name = "active")
    private Boolean active = true;
}

