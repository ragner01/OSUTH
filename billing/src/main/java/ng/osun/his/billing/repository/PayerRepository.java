package ng.osun.his.billing.repository;

import ng.osun.his.billing.domain.Payer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayerRepository extends JpaRepository<Payer, String> {
    Optional<Payer> findByPayerCode(String payerCode);
    List<Payer> findByActiveTrue();
    List<Payer> findByPayerType(String payerType);
}

