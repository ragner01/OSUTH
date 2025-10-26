package ng.osun.his.staffrota.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

import java.util.List;

/**
 * Rota entity for staff scheduling.
 */
@Entity
@Table(name = "rotas", indexes = {
    @Index(name = "idx_rota_period", columnList = "period_start, period_end"),
    @Index(name = "idx_rota_department", columnList = "department"),
    @Index(name = "idx_rota_status", columnList = "status")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Rota extends BaseEntity {

    @Column(name = "rota_period", nullable = false, length = 50)
    private String rotaPeriod; // "2024-01-WEEK1", "2024-01-MONTHLY"

    @Column(name = "period_start", nullable = false)
    private java.time.LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private java.time.LocalDate periodEnd;

    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // DRAFT, PUBLISHED, ARCHIVED

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "approved_at")
    private java.time.Instant approvedAt;

    @ElementCollection
    @CollectionTable(name = "rota_shifts", joinColumns = @JoinColumn(name = "rota_id"))
    private List<RotaShift> shifts;

    @Column(name = "notes", length = 1000)
    private String notes;
}
