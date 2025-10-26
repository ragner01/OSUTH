package ng.osun.his.pharmacy.repository;

import ng.osun.his.pharmacy.domain.StockLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Repository
public interface StockLedgerRepository extends JpaRepository<StockLedger, String> {
    List<StockLedger> findByMedicationCode(String medicationCode);
    List<StockLedger> findByMedicationCodeAndStatus(String medicationCode, String status);
    List<StockLedger> findByExpiryDateBeforeAndStatus(LocalDate threshold, String status);
    List<StockLedger> findByStatus(String status);
    List<StockLedger> findByBatchNumber(String batchNumber);
    
    @Query("SELECT s FROM StockLedger s WHERE s.medicationCode = :medicationCode AND s.status = :status ORDER BY s.expiryDate ASC")
    List<StockLedger> findByMedicationCodeAndStatusOrderByExpiryDate(String medicationCode, String status);
}

