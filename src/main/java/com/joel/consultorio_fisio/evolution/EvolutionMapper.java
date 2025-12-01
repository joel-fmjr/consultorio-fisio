package com.joel.consultorio_fisio.evolution;

import com.joel.consultorio_fisio.patient.Patient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EvolutionMapper {

    public EvolutionDTO toDTO(Evolution entity) {
        if (entity == null) {
            return null;
        }
        return EvolutionDTO.builder()
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

    public Evolution toEntity(EvolutionDTO dto) {
        if (dto == null) {
            return null;
        }

        Patient patient = Patient.builder()
                .id(dto.getPatientId())
                .build();

        return Evolution.builder()
                .id(dto.getId())
                .patient(patient)
                .evolutionDate(dto.getEvolutionDate())
                .conduct(dto.getConduct())
                .build();
    }

    public List<EvolutionDTO> toDTOList(List<Evolution> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
