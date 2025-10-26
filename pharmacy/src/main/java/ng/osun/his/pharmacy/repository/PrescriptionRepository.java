package ng.osun.his.pharmacy.repository;

import ng.osun.his.pharmacy.domain.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, String> {
    Optional<Prescription> findByPrescriptionNumber(String prescriptionNumber);
    List<Prescription> findByPatientIdOrderByPrescribedDateDesc(String patientId);
    List<Prescription> findByEncounterId(String encounterId);
    List<Prescription> findByStatus(String status);
}

