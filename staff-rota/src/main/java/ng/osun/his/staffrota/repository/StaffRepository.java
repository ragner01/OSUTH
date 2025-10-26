package ng.osun.his.staffrota.repository;

import ng.osun.his.staffrota.domain.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, String> {
    Optional<Staff> findByEmployeeNumber(String employeeNumber);
    Optional<Staff> findByUserRef(String userRef);
    List<Staff> findByDepartmentAndActiveTrue(String department);
    List<Staff> findByActiveTrue();
}

