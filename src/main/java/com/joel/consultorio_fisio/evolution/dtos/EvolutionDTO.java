package com.joel.consultorio_fisio.evolution.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvolutionDTO {

    private Long id;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    private LocalDateTime evolutionDate;

    private Integer evolutionNumber;

    @NotBlank(message = "Conduct is required")
    @Size(max = 5000, message = "Conduct cannot exceed 5000 characters")
    private String conduct;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String patientName;
}
