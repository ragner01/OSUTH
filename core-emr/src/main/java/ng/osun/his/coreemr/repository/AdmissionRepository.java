package ng.osun.his.coreemr.repository;

import ng.osun.his.coreemr.domain.Admission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdmissionRepository extends JpaRepository<Admission, String> {
    List<Admission> findByPatientId(String patientId);
    Optional<Admission> findByAdmissionNumber(String admissionNumber);
    List<Admission> findByStatus(String status);
}

