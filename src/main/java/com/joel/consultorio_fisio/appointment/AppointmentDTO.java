package com.joel.consultorio_fisio.appointment;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {

    private Long id;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Start time is required")
    @Future(message = "Appointment must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "Duration is required")
    private AppointmentDuration duration;

    @NotNull(message = "Payment status is required")
    private Boolean isPaid;

    @NotNull(message = "Cancellation status is required")
    private Boolean isCancelled;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    // Read-only fields - calculated/populated by backend
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String patientName;
}
