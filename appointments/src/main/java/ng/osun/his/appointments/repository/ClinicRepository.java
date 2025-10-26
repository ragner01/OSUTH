package ng.osun.his.appointments.repository;

import ng.osun.his.appointments.domain.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, String> {
    List<Clinic> findByActiveTrue();
    Optional<Clinic> findByCode(String code);
}

