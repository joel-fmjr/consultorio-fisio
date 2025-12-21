package com.joel.consultorio_fisio.appointment;

import com.joel.consultorio_fisio.appointment.dtos.AppointmentRequestDTO;
import com.joel.consultorio_fisio.appointment.dtos.AppointmentResponseDTO;
import com.joel.consultorio_fisio.patient.Patient;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppointmentMapper {

    public AppointmentResponseDTO toResponseDTO(Appointment entity) {
        if (entity == null) {
            return null;
        }
        return AppointmentResponseDTO.builder()
                .id(entity.getId())
                .patientId(entity.getPatient().getId())
                .patientName(entity.getPatient().getName())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .duration(entity.getDuration())
                .isPaid(entity.getIsPaid())
                .isCancelled(entity.getIsCancelled())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Appointment toEntity(AppointmentRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Patient patient = Patient.builder()
                .id(dto.getPatientId())
                .build();

        return Appointment.builder()
                .patient(patient)
                .startTime(dto.getStartTime())
                .duration(dto.getDuration())
                .isPaid(dto.getIsPaid())
                .isCancelled(dto.getIsCancelled())
                .notes(dto.getNotes())
                .build();
    }

    public List<AppointmentResponseDTO> toResponseDTOList(List<Appointment> entities) {
        return entities.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
