package ng.osun.his.billing.repository;

import ng.osun.his.billing.domain.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, String> {
    Optional<Claim> findByClaimNumber(String claimNumber);
    List<Claim> findByPatientId(String patientId);
    List<Claim> findByStatus(String status);
    List<Claim> findByPayerId(String payerId);
}

