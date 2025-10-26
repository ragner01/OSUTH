package ng.osun.his.staffrota.repository;

import ng.osun.his.staffrota.domain.HandoverChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HandoverChecklistRepository extends JpaRepository<HandoverChecklist, String> {
    Optional<HandoverChecklist> findByPatientIdAndStatus(String patientId, String status);
}

