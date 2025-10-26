package ng.osun.his.billing.repository;

import ng.osun.his.billing.domain.BillableEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillableEventRepository extends JpaRepository<BillableEvent, String> {
    List<BillableEvent> findByPatientIdAndBilledFalse(String patientId);
    List<BillableEvent> findByInvoiceId(String invoiceId);
}

