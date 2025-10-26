package ng.osun.his.appointments.domain;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ng.osun.his.platform.domain.BaseEntity;

import java.util.List;

/**
 * Visit queue entity for managing patient flow in clinics.
 */
@Entity
@Table(name = "visit_queues", indexes = {
    @Index(name = "idx_queue_clinic", columnList = "clinic_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class VisitQueue extends BaseEntity {

    @Column(name = "clinic_id", nullable = false, length = 36)
    private String clinicId;

    @ElementCollection
    @CollectionTable(name = "queue_items", joinColumns = @JoinColumn(name = "queue_id"))
    private List<QueueItem> items;

    @Column(name = "current_position")
    private Integer currentPosition = 0;

    @Column(name = "total_waiting")
    private Integer totalWaiting = 0;

    @Column(name = "average_wait_time_minutes")
    private Integer averageWaitTimeMinutes = 0;

    @Column(name = "last_updated")
    private java.time.Instant lastUpdated;
}

