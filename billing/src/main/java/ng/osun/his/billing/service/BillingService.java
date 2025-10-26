package ng.osun.his.billing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ng.osun.his.billing.domain.*;
import ng.osun.his.billing.repository.*;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for billing operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BillingService {

    private final PriceBookRepository priceBookRepository;
    private final BillableEventRepository billableEventRepository;
    private final InvoiceRepository invoiceRepository;
    private final PayerRepository payerRepository;
    private final PaymentRepository paymentRepository;
    private final ClaimRepository claimRepository;

    /**
     * Create invoice from billable events.
     */
    @Transactional
    public Invoice createInvoice(String patientId, String payerId, List<String> eventIds, Authentication auth) {
        log.info("Creating invoice for patient {} with {} events", patientId, eventIds.size());

        // Fetch un-billed events
        List<BillableEvent> events = billableEventRepository.findAllById(eventIds)
            .stream()
            .filter(e -> !e.getBilled() && e.getPatientId().equals(patientId))
            .collect(Collectors.toList());

        if (events.isEmpty()) {
            throw new IllegalStateException("No billable events found");
        }

        // Calculate totals
        BigDecimal subtotal = events.stream()
            .map(BillableEvent::getNetAmountNGN)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = events.stream()
            .map(BillableEvent::getDiscountAmountNGN)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = subtotal.subtract(discount);

        // Create invoice
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setPatientId(patientId);
        invoice.setPayerId(payerId);
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(30));
        invoice.setStatus("PENDING");
        invoice.setSubtotalNGN(subtotal);
        invoice.setDiscountAmountNGN(discount);
        invoice.setTotalAmountNGN(total);
        invoice.setBalanceNGN(total);
        invoice.setPaidAmountNGN(BigDecimal.ZERO);
        invoice.setCurrency("NGN");

        // Map billable events to line items
        List<InvoiceLineItem> lineItems = events.stream().map(e -> {
            InvoiceLineItem item = new InvoiceLineItem();
            item.setServiceCode(e.getServiceCode());
            item.setDescription(e.getServiceDescription());
            item.setQuantity(e.getQuantity());
            item.setUnitPrice(e.getUnitPriceNGN());
            item.setTotalAmount(e.getNetAmountNGN());
            item.setSourceEventId(e.getId());
            return item;
        }).collect(Collectors.toList());
        
        invoice.setLineItems(lineItems);

        // Payer info
        if (payerId != null) {
            payerRepository.findById(payerId).ifPresent(p -> {
                invoice.setPayerType(p.getPayerType());
                invoice.setPayerName(p.getPayerName());
            });
        } else {
            invoice.setPayerType("SELF_PAY");
            invoice.setPayerName("Self Pay");
        }

        invoice.setCreatedBy(auth.getName());

        Invoice saved = invoiceRepository.save(invoice);

        // Mark events as billed
        events.forEach(e -> {
            e.setBilled(true);
            e.setBilledAt(java.time.Instant.now());
            e.setBilledBy(auth.getName());
            e.setInvoiceId(saved.getId());
        });
        billableEventRepository.saveAll(events);

        log.info("Invoice {} created for patient {}", saved.getInvoiceNumber(), patientId);
        return saved;
    }

    /**
     * Process payment.
     */
    @Transactional
    public Payment processPayment(String invoiceId, BigDecimal amount, String method, 
                                   String reference, Authentication auth) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        Payment payment = new Payment();
        payment.setPaymentNumber(generatePaymentNumber());
        payment.setInvoiceId(invoiceId);
        payment.setPatientId(invoice.getPatientId());
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod(method);
        payment.setAmountNGN(amount);
        payment.setCurrency("NGN");
        payment.setReference(reference);
        payment.setStatus("PENDING");
        payment.setCashier(auth.getName());

        Payment saved = paymentRepository.save(payment);

        // Update invoice balance
        BigDecimal newPaidAmount = invoice.getPaidAmountNGN().add(amount);
        invoice.setPaidAmountNGN(newPaidAmount);
        invoice.setBalanceNGN(invoice.getTotalAmountNGN().subtract(newPaidAmount));

        if (invoice.getBalanceNGN().compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setStatus("PAID");
        } else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus("PARTIAL");
        }

        invoiceRepository.save(invoice);

        log.info("Payment {} processed for invoice {}", saved.getPaymentNumber(), invoiceId);
        return saved;
    }

    /**
     * Create claim for invoice.
     */
    @Transactional
    public Claim createClaim(String invoiceId, Authentication auth) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        if (invoice.getPayerId() == null) {
            throw new IllegalStateException("Cannot create claim for self-pay invoice");
        }

        Claim claim = new Claim();
        claim.setClaimNumber(generateClaimNumber());
        claim.setPatientId(invoice.getPatientId());
        claim.setPayerId(invoice.getPayerId());
        claim.setPayerName(invoice.getPayerName());
        claim.setInvoiceId(invoiceId);
        claim.setClaimDate(LocalDate.now());
        claim.setStatus("DRAFT");
        claim.setTotalAmountNGN(invoice.getTotalAmountNGN());
        claim.setCurrency("NGN");
        claim.setSubmittedBy(auth.getName());

        Claim saved = claimRepository.save(claim);
        log.info("Claim {} created for invoice {}", saved.getClaimNumber(), invoiceId);
        return saved;
    }

    /**
     * Submit claim to payer.
     */
    @Transactional
    public void submitClaim(String claimId, List<Object> attachments, Authentication auth) {
        Claim claim = claimRepository.findById(claimId)
            .orElseThrow(() -> new IllegalArgumentException("Claim not found"));

        claim.setStatus("SUBMITTED");
        claim.setSubmissionDate(java.time.Instant.now());
        claim.setSubmittedBy(auth.getName());
        // claim.setAttachments(attachments); // TODO: Map correctly

        claimRepository.save(claim);
        log.info("Claim {} submitted to payer", claimId);
    }

    private String generateInvoiceNumber() {
        return "INV-" + java.time.LocalDate.now().toString().replace("-", "") + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generatePaymentNumber() {
        return "PAY-" + java.time.LocalDate.now().toString().replace("-", "") + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateClaimNumber() {
        return "CLM-" + java.time.LocalDate.now().toString().replace("-", "") + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

