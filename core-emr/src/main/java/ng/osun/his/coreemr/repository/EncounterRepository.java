package ng.osun.his.coreemr.repository;

import ng.osun.his.coreemr.domain.Encounter;
import ng.osun.his.coreemr.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EncounterRepository extends JpaRepository<Encounter, String> {
    List<Encounter> findByPatient(Patient patient);
    List<Encounter> findByPatientIdOrderByStartDateDesc(String patientId);
    Optional<Encounter> findByEncounterNumber(String encounterNumber);
    List<Encounter> findByStatus(String status);
}

