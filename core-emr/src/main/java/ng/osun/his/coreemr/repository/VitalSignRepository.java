package ng.osun.his.coreemr.repository;

import ng.osun.his.coreemr.domain.Encounter;
import ng.osun.his.coreemr.domain.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VitalSignRepository extends JpaRepository<VitalSign, String> {
    List<VitalSign> findByEncounter(Encounter encounter);
    List<VitalSign> findByEncounterId(String encounterId);
}

