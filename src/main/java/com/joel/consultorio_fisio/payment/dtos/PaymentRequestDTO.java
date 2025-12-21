package com.joel.consultorio_fisio.payment.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    private Long appointmentId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Email(message = "Invalid payer email format")
    private String payerEmail;

    // Optional: Custom expiration in hours (min: 0.5, max: 720 = 30 days)
    @DecimalMin(value = "0.5", message = "Expiration must be at least 0.5 hours")
    @DecimalMax(value = "720", message = "Expiration cannot exceed 720 hours (30 days)")
    private BigDecimal expirationHours;
}
