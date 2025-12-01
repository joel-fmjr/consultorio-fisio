package com.joel.consultorio_fisio.appointment;

import com.joel.consultorio_fisio.patient.Patient;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppointmentMapper {

    public AppointmentDTO toDTO(Appointment entity) {
        if (entity == null) {
            return null;
        }
        return AppointmentDTO.builder()
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

    public Appointment toEntity(AppointmentDTO dto) {
        if (dto == null) {
            return null;
        }

        Patient patient = Patient.builder()
                .id(dto.getPatientId())
                .build();

        return Appointment.builder()
                .id(dto.getId())
                .patient(patient)
                .startTime(dto.getStartTime())
                .duration(dto.getDuration())
                .isPaid(dto.getIsPaid())
                .isCancelled(dto.getIsCancelled())
                .notes(dto.getNotes())
                .build();
    }

    public List<AppointmentDTO> toDTOList(List<Appointment> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
