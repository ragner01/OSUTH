package ng.osun.his.coreemr.repository;

import ng.osun.his.coreemr.domain.Diagnosis;
import ng.osun.his.coreemr.domain.Encounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, String> {
    List<Diagnosis> findByEncounter(Encounter encounter);
    List<Diagnosis> findByEncounterId(String encounterId);
}

