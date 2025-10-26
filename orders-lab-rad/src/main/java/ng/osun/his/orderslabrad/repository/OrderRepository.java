package ng.osun.his.orderslabrad.repository;

import ng.osun.his.orderslabrad.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByPatientIdOrderByOrderDateDesc(String patientId);
    List<Order> findByEncounterId(String encounterId);
    List<Order> findByStatus(String status);
    List<Order> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT o FROM Order o WHERE o.patientId = :patientId ORDER BY o.orderDate DESC")
    Page<Order> findRecentOrdersByPatient(String patientId, Pageable pageable);
}

