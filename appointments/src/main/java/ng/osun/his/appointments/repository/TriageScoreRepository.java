package ng.osun.his.appointments.repository;

import ng.osun.his.appointments.domain.TriageScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TriageScoreRepository extends JpaRepository<TriageScore, String> {
    Optional<TriageScore> findByAppointmentId(String appointmentId);
    List<TriageScore> findByPatientIdOrderByAssessedAtDesc(String patientId);
    List<TriageScore> findByPriorityLevel(String priorityLevel);
}

