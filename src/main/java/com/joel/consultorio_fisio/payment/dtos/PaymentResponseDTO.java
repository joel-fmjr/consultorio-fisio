package com.joel.consultorio_fisio.payment.dtos;

import com.joel.consultorio_fisio.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

    private Long id;
    private Long mercadoPagoPaymentId;
    private Long patientId;
    private Long appointmentId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String paymentMethod;
    private String qrCode;
    private String qrCodeBase64;
    private String ticketUrl;
    private String externalReference;
    private String statusDetail;
    private String description;
    private String payerEmail;
    private LocalDateTime dateOfExpiration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
