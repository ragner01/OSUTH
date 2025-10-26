package ng.osun.his.appointments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ng.osun.his.appointments.domain.QueueItem;
import ng.osun.his.appointments.domain.VisitQueue;
import ng.osun.his.appointments.repository.AppointmentRepository;
import ng.osun.his.appointments.repository.QueueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Smart queue service with ETA predictions and priority management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QueueService {

    private final QueueRepository queueRepository;
    private final AppointmentRepository appointmentRepository;
    private final TriageService triageService;

    /**
     * Add patient to queue with ETA prediction.
     */
    @Transactional
    public void addToQueue(String clinicId, String appointmentId, String patientId, String priorityLevel) {
        VisitQueue queue = queueRepository.findByClinicId(clinicId)
            .orElse(createNewQueue(clinicId));

        int queuePriority = triageService.getQueuePriority(priorityLevel);
        
        // Calculate ETA based on average wait time and position in queue
        int eta = calculateETA(queue, queuePriority);

        QueueItem newItem = new QueueItem();
        newItem.setPatientId(patientId);
        newItem.setAppointmentId(appointmentId);
        newItem.setPriority(queuePriority);
        newItem.setEtaMinutes(eta);
        newItem.setStatus("WAITING");
        newItem.setQueuedAt(Instant.now());
        newItem.setEstimatedStartTime(Instant.now().plus(eta, ChronoUnit.MINUTES));

        // Add to queue and sort by priority
        List<QueueItem> updatedItems = queue.getItems();
        updatedItems.add(newItem);
        updatedItems.sort(Comparator.comparingInt(QueueItem::getPriority));
        
        queue.setItems(updatedItems);
        queue.setTotalWaiting(updatedItems.size());
        queue.setLastUpdated(Instant.now());

        queueRepository.save(queue);

        log.info("Added patient {} to queue at clinic {} with ETA: {} minutes, priority: {}", 
            patientId, clinicId, eta, queuePriority);
    }

    /**
     * Calculate ETA based on queue position, priority, and historical data.
     */
    private int calculateETA(VisitQueue queue, int priority) {
        int waitingCount = queue.getTotalWaiting();
        int baseWait = queue.getAverageWaitTimeMinutes();

        // High priority patients get shorter ETA
        int priorityAdjustment = priority <= 2 ? -5 : 0;

        // Calculate based on position and average time
        int estimatedTime = (waitingCount * baseWait) / 2 + priorityAdjustment;

        // Ensure minimum wait time
        return Math.max(estimatedTime, 5);
    }

    /**
     * Update queue position and ETA for all waiting patients.
     */
    @Transactional
    public void updateQueueEstimates(String clinicId) {
        VisitQueue queue = queueRepository.findByClinicId(clinicId)
            .orElse(null);

        if (queue == null) return;

        List<QueueItem> waitingItems = queue.getItems().stream()
            .filter(item -> "WAITING".equals(item.getStatus()))
            .collect(Collectors.toList());

        for (int i = 0; i < waitingItems.size(); i++) {
            QueueItem item = waitingItems.get(i);
            int position = i + 1;
            
            // Recalculate ETA based on new position
            int priority = item.getPriority() != null ? item.getPriority() : 5;
            int newETA = calculateETA(queue, priority);
            
            item.setEtaMinutes(newETA);
            item.setEstimatedStartTime(Instant.now().plus(newETA, ChronoUnit.MINUTES));
        }

        queue.setItems(waitingItems);
        queue.setTotalWaiting(waitingItems.size());
        queue.setLastUpdated(Instant.now());

        queueRepository.save(queue);
    }

    /**
     * Get queue position for patient.
     */
    public Integer getQueuePosition(String clinicId, String patientId) {
        VisitQueue queue = queueRepository.findByClinicId(clinicId)
            .orElse(null);

        if (queue == null) return null;

        for (int i = 0; i < queue.getItems().size(); i++) {
            if (patientId.equals(queue.getItems().get(i).getPatientId())) {
                return i + 1;
            }
        }

        return null;
    }

    /**
     * Move next patient from queue to in-progress.
     */
    @Transactional
    public void processNext(String clinicId) {
        VisitQueue queue = queueRepository.findByClinicId(clinicId)
            .orElse(null);

        if (queue == null || queue.getItems().isEmpty()) return;

        // Get highest priority waiting item
        QueueItem nextItem = queue.getItems().stream()
            .filter(item -> "WAITING".equals(item.getStatus()))
            .min(Comparator.comparingInt((QueueItem item) -> item.getPriority() != null ? item.getPriority() : Integer.MAX_VALUE))
            .orElse(null);

        if (nextItem != null) {
            nextItem.setStatus("IN_PROGRESS");
            queue.setCurrentPosition(queue.getCurrentPosition() + 1);
            queue.setLastUpdated(Instant.now());

            queueRepository.save(queue);

            log.info("Processing next patient in queue at clinic {}", clinicId);
        }
    }

    /**
     * Mark patient as completed and update queue statistics.
     */
    @Transactional
    public void completePatient(String clinicId, String patientId) {
        VisitQueue queue = queueRepository.findByClinicId(clinicId)
            .orElse(null);

        if (queue == null) return;

        queue.getItems().stream()
            .filter(item -> patientId.equals(item.getPatientId()))
            .forEach(item -> {
                if ("IN_PROGRESS".equals(item.getStatus())) {
                    long actualWaitTime = ChronoUnit.MINUTES.between(item.getQueuedAt(), Instant.now());
                    
                    // Update average wait time
                    int currentAvg = queue.getAverageWaitTimeMinutes();
                    int newAvg = (int) ((currentAvg + actualWaitTime) / 2);
                    queue.setAverageWaitTimeMinutes(newAvg);

                    item.setStatus("COMPLETED");
                }
            });

        // Remove completed items
        List<QueueItem> filtered = queue.getItems().stream()
            .filter(item -> !"COMPLETED".equals(item.getStatus()))
            .collect(Collectors.toList());
        queue.setItems(filtered);

        queue.setTotalWaiting(queue.getItems().size());
        queue.setLastUpdated(Instant.now());

        queueRepository.save(queue);
    }

    /**
     * Create new queue for clinic.
     */
    private VisitQueue createNewQueue(String clinicId) {
        VisitQueue queue = new VisitQueue();
        queue.setClinicId(clinicId);
        queue.setItems(new ArrayList<>());
        queue.setCurrentPosition(0);
        queue.setTotalWaiting(0);
        queue.setAverageWaitTimeMinutes(15);
        queue.setCreatedAt(Instant.now());
        queue.setUpdatedAt(Instant.now());
        return queue;
    }
}

