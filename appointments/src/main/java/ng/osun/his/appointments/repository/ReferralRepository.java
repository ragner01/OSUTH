package ng.osun.his.appointments.repository;

import ng.osun.his.appointments.domain.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, String> {
    List<Referral> findByPatientId(String patientId);
    Optional<Referral> findByReferralNumber(String referralNumber);
    List<Referral> findByStatus(String status);
    List<Referral> findByType(String type);
}

