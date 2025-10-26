package ng.osun.his.orderslabrad.repository;

import ng.osun.his.orderslabrad.domain.ImagingStudy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImagingStudyRepository extends JpaRepository<ImagingStudy, String> {
    Optional<ImagingStudy> findByStudyNumber(String studyNumber);
    List<ImagingStudy> findByOrderId(String orderId);
    List<ImagingStudy> findByPatientIdOrderByStudyDateDesc(String patientId);
    List<ImagingStudy> findByStatus(String status);
}

