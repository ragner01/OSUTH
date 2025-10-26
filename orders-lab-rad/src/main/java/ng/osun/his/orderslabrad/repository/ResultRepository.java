package ng.osun.his.orderslabrad.repository;

import ng.osun.his.orderslabrad.domain.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, String> {
    Optional<Result> findByResultNumber(String resultNumber);
    List<Result> findByOrderItemId(String orderItemId);
    List<Result> findByPatientIdOrderByPerformedAtDesc(String patientId);
    
    @Query("SELECT r FROM Result r WHERE r.patientId = :patientId ORDER BY r.performedAt DESC")
    Page<Result> findRecentResultsByPatient(String patientId, Pageable pageable);
    
    List<Result> findByCriticalFlagTrue();
    List<Result> findByCriticalValueTrue();
    List<Result> findByVerifiedFalse();
}

