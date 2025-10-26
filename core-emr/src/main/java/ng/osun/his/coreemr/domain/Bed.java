package ng.osun.his.coreemr.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

/**
 * Bed entity.
 */
@Entity
@Table(name = "beds", indexes = {
    @Index(name = "idx_beds_ward", columnList = "ward_id"),
    @Index(name = "idx_beds_status", columnList = "status")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Bed extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    private Ward ward;

    @Column(name = "code", unique = true, length = 50)
    private String code;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // AVAILABLE, OCCUPIED, RESERVED, OUT_OF_SERVICE

    @Column(name = "room_number", length = 50)
    private String roomNumber;

    @Column(name = "bed_number", length = 20)
    private String bedNumber; // Within the room

    @Column(name = "isolation")
    private Boolean isolation = false;

    @ElementCollection
    private java.util.List<String> isolationFlags; // CONTACT, DROPLET, AIRBORNE

    @Column(name = "special_equipment", length = 500)
    private String specialEquipment; // Ventilator, Monitor, etc.

    @Column(name = "current_patient_id")
    private String currentPatientId;

    @Column(name = "notes", length = 500)
    private String notes;
}

