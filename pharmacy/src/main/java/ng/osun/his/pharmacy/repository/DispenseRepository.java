package ng.osun.his.pharmacy.repository;

import ng.osun.his.pharmacy.domain.Dispense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DispenseRepository extends JpaRepository<Dispense, String> {
    Optional<Dispense> findByDispenseNumber(String dispenseNumber);
    List<Dispense> findByPrescriptionId(String prescriptionId);
    List<Dispense> findByPatientIdOrderByDispenseDateDesc(String patientId);
    List<Dispense> findByPaymentStatus(String paymentStatus);
}

