package ng.osun.his.pharmacy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ng.osun.his.pharmacy.domain.DispenseLine;
import ng.osun.his.pharmacy.domain.StockLedger;
import ng.osun.his.pharmacy.repository.StockLedgerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * FEFO (First Expired First Out) dispensing service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FEFODispensingService {

    private final StockLedgerRepository stockLedgerRepository;

    /**
     * Select batches using FEFO principle for dispensing.
     */
    @Transactional
    public List<FEFOBatch> selectBatchesForDispensing(String medicationCode, int requestedQuantity) {
        // Get available stock for medication, ordered by expiry (FEFO)
        List<StockLedger> availableStock = stockLedgerRepository
            .findByMedicationCodeAndStatusOrderByExpiryDate(medicationCode, "AVAILABLE");

        List<FEFOBatch> selectedBatches = new ArrayList<>();
        int quantityNeeded = requestedQuantity;

        for (StockLedger stock : availableStock) {
            if (quantityNeeded <= 0) break;

            int availableQuantity = stock.getQuantity();
            int quantityToTake = Math.min(availableQuantity, quantityNeeded);

            selectedBatches.add(new FEFOBatch(
                stock.getBatchNumber(),
                stock.getExpiryDate(),
                quantityToTake,
                stock.getUnitCostNGN(),
                stock.getId()
            ));

            quantityNeeded -= quantityToTake;
        }

        if (quantityNeeded > 0) {
            throw new IllegalStateException(
                String.format("Insufficient stock for %s. Requested: %d, Available: %d",
                    medicationCode, requestedQuantity, requestedQuantity - quantityNeeded));
        }

        log.info("FEFO selection for {}: {} batches selected", medicationCode, selectedBatches.size());
        return selectedBatches;
    }

    /**
     * Check for near-expiry items (within 30 days).
     */
    public List<StockLedger> findNearExpiryItems(int daysThreshold) {
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);

        return stockLedgerRepository.findByExpiryDateBeforeAndStatus(
            thresholdDate, "AVAILABLE");
    }

    /**
     * Check for low stock levels.
     */
    public List<String> findLowStockItems(int minThreshold) {
        List<String> lowStockMeds = new ArrayList<>();

        // Get all available medications with their total quantities
        Map<String, Integer> medicationTotals = stockLedgerRepository
            .findAll()
            .stream()
            .filter(s -> "AVAILABLE".equals(s.getStatus()))
            .collect(Collectors.groupingBy(
                StockLedger::getMedicationCode,
                Collectors.summingInt(StockLedger::getQuantity)
            ));

        medicationTotals.forEach((med, quantity) -> {
            if (quantity < minThreshold) {
                lowStockMeds.add(med);
            }
        });

        return lowStockMeds;
    }

    public static class FEFOBatch {
        private final String batchNumber;
        private final LocalDate expiryDate;
        private final int quantity;
        private final BigDecimal unitCost;
        private final String stockId;

        public FEFOBatch(String batchNumber, LocalDate expiryDate, int quantity, 
                        BigDecimal unitCost, String stockId) {
            this.batchNumber = batchNumber;
            this.expiryDate = expiryDate;
            this.quantity = quantity;
            this.unitCost = unitCost;
            this.stockId = stockId;
        }

        public String getBatchNumber() { return batchNumber; }
        public LocalDate getExpiryDate() { return expiryDate; }
        public int getQuantity() { return quantity; }
        public BigDecimal getUnitCost() { return unitCost; }
        public String getStockId() { return stockId; }
    }
}

