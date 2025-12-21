package com.joel.consultorio_fisio.payment.dtos;

import com.joel.consultorio_fisio.payment.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class PaymentDTO {

    private Long id;

    private Long mercadoPagoPaymentId;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    private Long appointmentId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private PaymentStatus status;

    private String paymentMethod;

    private String qrCode;

    private String qrCodeBase64;

    private String ticketUrl;

    private String externalReference;

    private String statusDetail;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Email(message = "Invalid email format")
    private String payerEmail;

    private LocalDateTime dateOfExpiration;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
