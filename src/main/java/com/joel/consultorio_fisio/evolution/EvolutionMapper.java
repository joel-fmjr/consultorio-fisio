package com.joel.consultorio_fisio.evolution;

import com.joel.consultorio_fisio.patient.Patient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EvolutionMapper {

    public EvolutionResponseDTO toResponseDTO(Evolution entity) {
        if (entity == null) {
            return null;
        }
        return EvolutionResponseDTO.builder()
                .id(entity.getId())
                .patientId(entity.getPatient().getId())
                .patientName(entity.getPatient().getName())
                .evolutionDate(entity.getEvolutionDate())
                .evolutionNumber(entity.getEvolutionNumber())
                .conduct(entity.getConduct())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Evolution toEntity(EvolutionRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Patient patient = Patient.builder()
                .id(dto.getPatientId())
                .build();

        return Evolution.builder()
                .patient(patient)
                .conduct(dto.getConduct())
                .build();
    }

    public List<EvolutionResponseDTO> toResponseDTOList(List<Evolution> entities) {
        return entities.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
