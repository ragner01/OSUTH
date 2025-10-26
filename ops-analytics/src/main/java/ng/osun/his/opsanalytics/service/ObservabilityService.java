package ng.osun.his.opsanalytics.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Service for observability metrics and tracing.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ObservabilityService {

    private final MeterRegistry meterRegistry;

    /**
     * Record OPD throughput metrics.
     */
    public void recordOpdThroughput(String clinic, int count, Duration duration) {
        meterRegistry.counter("opd.throughput", "clinic", clinic).increment(count);
        meterRegistry.timer("opd.visit.duration", "clinic", clinic)
            .record(duration);
    }

    /**
     * Record lab TAT (Turnaround Time).
     */
    public void recordLabTat(String testType, Duration duration) {
        meterRegistry.timer("lab.tat", "test_type", testType)
            .record(duration);
        
        log.info("Lab TAT for {}: {}ms", 
            testType, duration.toMillis());
    }

    /**
     * Record pharmacy turn metrics.
     */
    public void recordPharmacyTurn(String status, Duration duration) {
        meterRegistry.timer("pharmacy.turn", "status", status)
            .record(duration);
    }

    /**
     * Record API latency with percentiles.
     */
    public Timer.Sample startApiTimer(String endpoint) {
        return Timer.start(meterRegistry);
    }

    public void stopApiTimer(Timer.Sample sample, String endpoint) {
        Timer timer = Timer.builder("api.latency")
            .tag("endpoint", endpoint)
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(meterRegistry);
        sample.stop(timer);
    }

    /**
     * Record error budget.
     */
    public void recordError(String service, String errorType, String errorMessage) {
        meterRegistry.counter("errors.total", "service", service, "type", errorType)
            .increment();
        log.error("Error in {}: {} - {}", service, errorType, errorMessage);
    }

    /**
     * Check SLO compliance.
     */
    public boolean checkSloCompliance(String sloName, double target) {
        try {
            double actual = meterRegistry.get(sloName).counter().count();
            boolean compliant = actual >= target;
            
            if (!compliant) {
                log.warn("SLO {} violated: {} < {}", sloName, actual, target);
                meterRegistry.counter("slo.violations", "slo", sloName).increment();
            }
            
            return compliant;
        } catch (Exception e) {
            log.error("Error checking SLO compliance for {}", sloName, e);
            return false;
        }
    }
}
