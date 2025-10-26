package ng.osun.his.coreemr.repository;

import ng.osun.his.coreemr.domain.ClinicalNote;
import ng.osun.his.coreemr.domain.Encounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClinicalNoteRepository extends JpaRepository<ClinicalNote, String> {
    List<ClinicalNote> findByEncounter(Encounter encounter);
    List<ClinicalNote> findByEncounterId(String encounterId);
}

