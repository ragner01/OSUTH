package ng.osun.his.orderslabrad.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ng.osun.his.orderslabrad.domain.Order;
import ng.osun.his.orderslabrad.repository.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Service for order workflow management with Kafka event publishing.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderWorkflowService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String ORDER_EVENTS_TOPIC = "order.events";

    /**
     * Transition order from PLACED → IN_PROGRESS.
     */
    @Transactional
    public void startProcessing(String orderId, Authentication authentication) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalStateException("Order not found"));

        if (!"PLACED".equals(order.getStatus())) {
            throw new IllegalStateException("Order cannot be started from status: " + order.getStatus());
        }

        order.setStatus("IN_PROGRESS");
        order.setStartedDate(Instant.now());

        orderRepository.save(order);
        publishOrderEvent(order, "IN_PROGRESS", authentication.getName());

        log.info("Order {} started processing by user {}", orderId, authentication.getName());
    }

    /**
     * Transition order from IN_PROGRESS → RESULTED.
     */
    @Transactional
    public void markResulted(String orderId, Authentication authentication) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalStateException("Order not found"));

        if (!"IN_PROGRESS".equals(order.getStatus())) {
            throw new IllegalStateException("Order cannot be marked resulted from status: " + order.getStatus());
        }

        order.setStatus("RESULTED");
        order.setResultedDate(Instant.now());

        orderRepository.save(order);
        publishOrderEvent(order, "RESULTED", authentication.getName());

        log.info("Order {} marked as resulted by user {}", orderId, authentication.getName());
    }

    /**
     * Transition order from RESULTED → VERIFIED.
     */
    @Transactional
    public void verify(String orderId, Authentication authentication) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalStateException("Order not found"));

        if (!"RESULTED".equals(order.getStatus())) {
            throw new IllegalStateException("Order cannot be verified from status: " + order.getStatus());
        }

        order.setStatus("VERIFIED");
        order.setVerifiedDate(Instant.now());

        orderRepository.save(order);
        publishOrderEvent(order, "VERIFIED", authentication.getName());

        log.info("Order {} verified by user {}", orderId, authentication.getName());
    }

    /**
     * Cancel order.
     */
    @Transactional
    public void cancel(String orderId, String reason, Authentication authentication) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalStateException("Order not found"));

        order.setStatus("CANCELLED");

        orderRepository.save(order);
        publishOrderEvent(order, "CANCELLED", authentication.getName());

        log.info("Order {} cancelled by user {} reason: {}", orderId, authentication.getName(), reason);
    }

    /**
     * Publish order status change event to Kafka.
     */
    private void publishOrderEvent(Order order, String newStatus, String userId) {
        String eventJson = String.format(
            "{\"orderId\":\"%s\",\"orderNumber\":\"%s\",\"patientId\":\"%s\",\"oldStatus\":\"%s\",\"newStatus\":\"%s\",\"userId\":\"%s\",\"timestamp\":\"%s\"}",
            order.getId(),
            order.getOrderNumber(),
            order.getPatientId(),
            order.getStatus(),
            newStatus,
            userId,
            Instant.now()
        );

        kafkaTemplate.send(ORDER_EVENTS_TOPIC, order.getId(), eventJson);
        log.debug("Published order event: {} for order {}", newStatus, order.getId());
    }
}

