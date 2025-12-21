package com.joel.consultorio_fisio.evolution.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvolutionResponseDTO {

    private Long id;
    private Long patientId;
    private String patientName;
    private LocalDateTime evolutionDate;
    private Integer evolutionNumber;
    private String conduct;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
