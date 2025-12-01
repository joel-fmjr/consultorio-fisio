package com.joel.consultorio_fisio.payment;

import com.joel.consultorio_fisio.appointment.Appointment;
import com.joel.consultorio_fisio.appointment.AppointmentRepository;
import com.joel.consultorio_fisio.exception.PaymentProcessingException;
import com.joel.consultorio_fisio.exception.ResourceNotFoundException;
import com.joel.consultorio_fisio.patient.Patient;
import com.joel.consultorio_fisio.patient.PatientRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentMapper paymentMapper;

    public List<PaymentDTO> findAll() {
        return paymentMapper.toDTOList(paymentRepository.findAll());
    }

    public PaymentDTO findById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return paymentMapper.toDTO(payment);
    }

    public PaymentDTO findByMercadoPagoId(Long mercadoPagoId) {
        Payment payment = paymentRepository.findByMercadoPagoPaymentId(mercadoPagoId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with Mercado Pago ID: " + mercadoPagoId));
        return paymentMapper.toDTO(payment);
    }

    public List<PaymentDTO> findByPatientId(Long patientId) {
        return paymentMapper.toDTOList(paymentRepository.findByPatientId(patientId));
    }

    public List<PaymentDTO> findByStatus(PaymentStatus status) {
        return paymentMapper.toDTOList(paymentRepository.findByStatus(status));
    }

    @Transactional
    public PIXPaymentResponse createPIXPayment(PIXPaymentRequest request) {
        log.info("Creating PIX payment for patient ID: {}", request.getPatientId());

        try {
            // 1. Validate patient exists
            Patient patient = patientRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));

            // 2. If appointmentId provided, validate it exists
            Appointment appointment = null;
            if (request.getAppointmentId() != null) {
                appointment = appointmentRepository.findById(request.getAppointmentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + request.getAppointmentId()));
            }

            // 3. Generate unique external reference
            String externalReference = "FISIO-" + UUID.randomUUID().toString();
            Map<String, String> customHeaders = new HashMap<>();
            customHeaders.put("x-idempotency-key", externalReference);
            MPRequestOptions requestOptions = MPRequestOptions.builder()
                    .customHeaders(customHeaders)
                    .build();

            // 4. Calculate expiration date
            BigDecimal expirationHours = request.getExpirationHours() != null
                    ? request.getExpirationHours()
                    : BigDecimal.valueOf(24);
            LocalDateTime expirationDate = LocalDateTime.now()
                    .plusHours(expirationHours.longValue())
                    .plusMinutes((expirationHours.remainder(BigDecimal.ONE)
                            .multiply(BigDecimal.valueOf(60))).longValue());

            // 5. Create Mercado Pago payment request
            PaymentClient client = new PaymentClient();
            PaymentCreateRequest mpRequest = PaymentCreateRequest.builder()
                    .transactionAmount(request.getAmount())
                    .description(request.getDescription() != null
                            ? request.getDescription()
                            : "Consulta Fisioterapia")
                    .paymentMethodId("pix")
                    .externalReference(externalReference)
                    .payer(PaymentPayerRequest.builder()
                            .email(request.getPayerEmail() != null
                                    ? request.getPayerEmail()
                                    : patient.getEmail())
                            .firstName(patient.getName())
                            .build())
                    .build();

            // 6. Call Mercado Pago API
            log.info("Calling Mercado Pago API to create payment");
            com.mercadopago.resources.payment.Payment mpPayment =
                    client.create(mpRequest, requestOptions);

            log.info("Mercado Pago payment created successfully. ID: {}", mpPayment.getId());

            // 7. Extract PIX data from response
            String qrCode = mpPayment.getPointOfInteraction()
                    .getTransactionData()
                    .getQrCode();
            String qrCodeBase64 = mpPayment.getPointOfInteraction()
                    .getTransactionData()
                    .getQrCodeBase64();
            String ticketUrl = mpPayment.getPointOfInteraction()
                    .getTransactionData()
                    .getTicketUrl();

            // 8. Save payment to database
            Payment payment = Payment.builder()
                    .mercadoPagoPaymentId(mpPayment.getId())
                    .patient(patient)
                    .appointment(appointment)
                    .amount(request.getAmount())
                    .status(PaymentStatus.valueOf(mpPayment.getStatus().toUpperCase()))
                    .paymentMethod("pix")
                    .qrCode(qrCode)
                    .qrCodeBase64(qrCodeBase64)
                    .ticketUrl(ticketUrl)
                    .externalReference(externalReference)
                    .statusDetail(mpPayment.getStatusDetail())
                    .description(mpRequest.getDescription())
                    .payerEmail(mpRequest.getPayer().getEmail())
                    .dateOfExpiration(expirationDate)
                    .build();

            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment saved to database with ID: {}", savedPayment.getId());

            // 9. Return PIX payment response
            return paymentMapper.toPIXPaymentResponse(savedPayment);

        } catch (MPApiException e) {
            log.error("Mercado Pago API error: Status={}, Message={}", e.getStatusCode(), e.getMessage(), e);
            throw new PaymentProcessingException("Mercado Pago API error: " + e.getMessage(), e);
        } catch (MPException e) {
            log.error("Mercado Pago SDK error: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Error processing payment: " + e.getMessage(), e);
        }
    }

    @Transactional
    public PaymentDTO updatePaymentStatus(Long mercadoPagoId, PaymentStatus status, String statusDetail) {
        log.info("Updating payment status for Mercado Pago ID: {} to {}", mercadoPagoId, status);

        Payment payment = paymentRepository.findByMercadoPagoPaymentId(mercadoPagoId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with Mercado Pago ID: " + mercadoPagoId));

        payment.setStatus(status);
        payment.setStatusDetail(statusDetail);

        Payment updatedPayment = paymentRepository.save(payment);
        log.info("Payment status updated successfully");

        return paymentMapper.toDTO(updatedPayment);
    }

    @Transactional
    public void processWebhookNotification(Long paymentId) {
        log.info("Processing webhook notification for payment ID: {}", paymentId);

        try {
            // Fetch the latest payment status from Mercado Pago
            PaymentClient client = new PaymentClient();
            com.mercadopago.resources.payment.Payment mpPayment = client.get(paymentId);

            log.info("Retrieved payment from Mercado Pago. Status: {}", mpPayment.getStatus());

            // Update payment status in database
            Payment payment = paymentRepository.findByMercadoPagoPaymentId(paymentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found with Mercado Pago ID: " + paymentId));

            payment.setStatus(PaymentStatus.valueOf(mpPayment.getStatus().toUpperCase()));
            payment.setStatusDetail(mpPayment.getStatusDetail());

            paymentRepository.save(payment);
            log.info("Payment status updated from webhook notification");

        } catch (MPApiException e) {
            log.error("Mercado Pago API error during webhook processing: Status={}, Message={}",
                    e.getStatusCode(), e.getMessage(), e);
            throw new PaymentProcessingException("Error processing webhook: " + e.getMessage(), e);
        } catch (MPException e) {
            log.error("Mercado Pago SDK error during webhook processing: {}", e.getMessage(), e);
            throw new PaymentProcessingException("Error processing webhook: " + e.getMessage(), e);
        }
    }
}
