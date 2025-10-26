package ng.osun.his.coreemr.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

import java.util.List;

/**
 * Ward entity.
 */
@Entity
@Table(name = "wards")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Ward extends BaseEntity {

    @Column(name = "code", unique = true, length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "type", length = 50)
    private String type; // GENERAL, PEDIATRIC, OBSTETRIC, INTENSIVE_CARE, EMERGENCY

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "occupied")
    private Integer occupied = 0;

    @Column(name = "floor")
    private Integer floor;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "active")
    private Boolean active = true;

    @OneToMany(mappedBy = "ward", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bed> beds;
}

