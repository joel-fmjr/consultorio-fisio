package com.joel.consultorio_fisio.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO {

    private Long id;
    private Long patientId;
    private String patientName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AppointmentDuration duration;
    private Boolean isPaid;
    private Boolean isCancelled;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
