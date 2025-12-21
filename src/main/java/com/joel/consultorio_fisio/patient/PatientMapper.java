package com.joel.consultorio_fisio.patient;

import com.joel.consultorio_fisio.patient.dtos.PatientRequestDTO;
import com.joel.consultorio_fisio.patient.dtos.PatientResponseDTO;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PatientMapper {

    public PatientResponseDTO toResponseDTO(Patient entity) {
        if (entity == null) {
            return null;
        }
        return PatientResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .cpf(entity.getCpf())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .birthDate(entity.getBirthDate())
                .address(entity.getAddress())
                .medicalHistory(entity.getMedicalHistory())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Patient toEntity(PatientRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Patient.builder()
                .name(dto.getName())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .birthDate(dto.getBirthDate())
                .address(dto.getAddress())
                .medicalHistory(dto.getMedicalHistory())
                .build();
    }

    public List<PatientResponseDTO> toResponseDTOList(List<Patient> entities) {
        return entities.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
