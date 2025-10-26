package ng.osun.his.staffrota.repository;

import ng.osun.his.staffrota.domain.ShiftTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftTemplateRepository extends JpaRepository<ShiftTemplate, String> {
    List<ShiftTemplate> findByDepartment(String department);
    List<ShiftTemplate> findByDepartmentAndActiveTrue(String department);
}

