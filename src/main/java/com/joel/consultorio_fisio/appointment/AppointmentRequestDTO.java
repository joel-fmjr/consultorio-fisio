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
public class AppointmentRequestDTO {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Start time is required")
    @Future(message = "Appointment must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "Duration is required")
    private AppointmentDuration duration;

    @Builder.Default
    private Boolean isPaid = false;

    @Builder.Default
    private Boolean isCancelled = false;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
}
