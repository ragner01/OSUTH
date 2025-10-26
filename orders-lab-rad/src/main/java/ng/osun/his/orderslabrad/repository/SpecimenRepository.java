package ng.osun.his.orderslabrad.repository;

import ng.osun.his.orderslabrad.domain.Specimen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecimenRepository extends JpaRepository<Specimen, String> {
    Optional<Specimen> findBySpecimenNumber(String specimenNumber);
    List<Specimen> findByOrderId(String orderId);
    List<Specimen> findByOrderItemId(String orderItemId);
    List<Specimen> findByStatus(String status);
}

