package com.joel.consultorio_fisio.appointment;

import com.joel.consultorio_fisio.exception.ResourceNotFoundException;
import com.joel.consultorio_fisio.patient.Patient;
import com.joel.consultorio_fisio.patient.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;
    private final AppointmentMapper mapper;
    private final PatientRepository patientRepository;

    public List<AppointmentDTO> findAll() {
        return mapper.toDTOList(repository.findAll());
    }

    public AppointmentDTO findById(Long id) {
        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        return mapper.toDTO(appointment);
    }

    public List<AppointmentDTO> findByPatientId(Long patientId) {
        return mapper.toDTOList(repository.findByPatientId(patientId));
    }

    @Transactional
    public AppointmentDTO create(AppointmentDTO dto) {
        // Validate patient exists
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + dto.getPatientId()));

        Appointment appointment = mapper.toEntity(dto);
        appointment.setPatient(patient);

        Appointment saved = repository.save(appointment);
        return mapper.toDTO(saved);
    }

    @Transactional
    public AppointmentDTO update(Long id, AppointmentDTO dto) {
        Appointment existingAppointment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        // Validate patient exists if changing patient
        if (!existingAppointment.getPatient().getId().equals(dto.getPatientId())) {
            Patient newPatient = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + dto.getPatientId()));
            existingAppointment.setPatient(newPatient);
        }

        // Update fields manually
        existingAppointment.setStartTime(dto.getStartTime());
        existingAppointment.setDuration(dto.getDuration());
        existingAppointment.setIsPaid(dto.getIsPaid());
        existingAppointment.setIsCancelled(dto.getIsCancelled());
        existingAppointment.setNotes(dto.getNotes());

        Appointment updated = repository.save(existingAppointment);
        return mapper.toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Appointment not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public AppointmentDTO markAsPaid(Long id) {
        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        appointment.setIsPaid(true);
        Appointment updated = repository.save(appointment);
        return mapper.toDTO(updated);
    }

    @Transactional
    public AppointmentDTO cancelAppointment(Long id) {
        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        appointment.setIsCancelled(true);
        Appointment updated = repository.save(appointment);
        return mapper.toDTO(updated);
    }
}
