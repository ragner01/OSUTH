package ng.osun.his.staffrota.repository;

import ng.osun.his.staffrota.domain.Rota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RotaRepository extends JpaRepository<Rota, String> {
    Optional<Rota> findByRotaPeriod(String rotaPeriod);
    List<Rota> findByDepartment(String department);
    List<Rota> findByStatus(String status);
    boolean existsByDepartmentAndPeriodStartAndPeriodEnd(String department, LocalDate periodStart, LocalDate periodEnd);
}

