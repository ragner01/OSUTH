package ng.osun.his.staffrota.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RotaShift {
    @Column(name = "staff_id")
    private String staffId;

    @Column(name = "shift_date")
    private LocalDate shiftDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "shift_type")
    private String shiftType;

    @Column(name = "location")
    private String location;

    @Column(name = "status")
    private String status; // ASSIGNED, CONFIRMED, ABSENT, REPLACED
}

