package ng.osun.his.coreemr.repository;

import ng.osun.his.coreemr.domain.Allergy;
import ng.osun.his.coreemr.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, String> {
    List<Allergy> findByPatient(Patient patient);
    List<Allergy> findByPatientIdAndStatus(String patientId, String status);
}

