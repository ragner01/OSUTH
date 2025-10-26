package ng.osun.his.billing.repository;

import ng.osun.his.billing.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByPaymentNumber(String paymentNumber);
    List<Payment> findByInvoiceId(String invoiceId);
    List<Payment> findByPatientId(String patientId);
}

