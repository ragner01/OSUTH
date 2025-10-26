package ng.osun.his.pharmacy.repository;

import ng.osun.his.pharmacy.domain.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, String> {
    Optional<Medication> findByCode(String code);
    List<Medication> findByActiveTrue();
    List<Medication> findByNameContainingIgnoreCase(String name);
}

