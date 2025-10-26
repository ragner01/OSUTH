package ng.osun.his.staffrota.repository;

import ng.osun.his.staffrota.domain.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, String> {
    List<Leave> findByStaffId(String staffId);
    List<Leave> findByStatus(String status);
}

