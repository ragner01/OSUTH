package ng.osun.his.staffrota.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HandoverItem {
    @Column(name = "item_category")
    private String itemCategory; // VITALS, MEDICATIONS, ALLERGIES, DIAGNOSIS, ACTIVE_ORDERS

    @Column(name = "item_description")
    private String itemDescription;

    @Column(name = "completed")
    private Boolean completed = false;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "completed_by")
    private String completedBy;

    @Column(name = "notes")
    private String notes;
}

