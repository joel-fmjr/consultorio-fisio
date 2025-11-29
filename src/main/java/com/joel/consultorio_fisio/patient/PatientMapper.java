package com.joel.consultorio_fisio.patient;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PatientMapper {

    public PatientDTO toDTO(Patient entity) {
        if (entity == null) {
            return null;
        }
        return PatientDTO.builder()
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

    public Patient toEntity(PatientDTO dto) {
        if (dto == null) {
            return null;
        }
        return Patient.builder()
                .id(dto.getId())
                .name(dto.getName())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .birthDate(dto.getBirthDate())
                .address(dto.getAddress())
                .medicalHistory(dto.getMedicalHistory())
                .build();
    }

    public List<PatientDTO> toDTOList(List<Patient> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
