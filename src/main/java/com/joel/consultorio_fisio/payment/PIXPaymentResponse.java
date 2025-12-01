package com.joel.consultorio_fisio.payment;

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
public class PIXPaymentResponse {

    private Long id;

    private Long mercadoPagoPaymentId;

    private BigDecimal amount;

    private PaymentStatus status;

    private String statusDetail;

    private String qrCode;

    private String qrCodeBase64;

    private String ticketUrl;

    private String externalReference;

    private LocalDateTime dateOfExpiration;

    private LocalDateTime createdAt;
}
