package ng.osun.his.appointments.repository;

import ng.osun.his.appointments.domain.VisitQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<VisitQueue, String> {
    Optional<VisitQueue> findByClinicId(String clinicId);
}

