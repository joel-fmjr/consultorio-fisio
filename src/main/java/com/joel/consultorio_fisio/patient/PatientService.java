package com.joel.consultorio_fisio.patient;

import com.joel.consultorio_fisio.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository repository;
    private final PatientMapper mapper;

    public List<PatientResponseDTO> findAll() {
        return mapper.toResponseDTOList(repository.findAll());
    }

    public PatientResponseDTO findById(Long id) {
        Patient patient = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        return mapper.toResponseDTO(patient);
    }

    public List<PatientResponseDTO> findByName(String name) {
        return mapper.toResponseDTOList(repository.findByNameContainingIgnoreCase(name));
    }

    @Transactional
    public PatientResponseDTO create(PatientRequestDTO dto) {
        // Validate unique CPF only if provided
        if (dto.getCpf() != null && !dto.getCpf().isBlank() &&
            repository.findByCpf(dto.getCpf()).isPresent()) {
            throw new IllegalArgumentException("CPF already registered");
        }

        // Validate unique email only if provided
        if (dto.getEmail() != null && !dto.getEmail().isBlank() &&
            repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        Patient patient = mapper.toEntity(dto);
        Patient saved = repository.save(patient);
        return mapper.toResponseDTO(saved);
    }

    @Transactional
    public PatientResponseDTO update(Long id, PatientRequestDTO dto) {
        Patient existingPatient = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        // Check if CPF is being changed and if it's already in use (only if provided)
        if (dto.getCpf() != null && !dto.getCpf().isBlank() &&
            (existingPatient.getCpf() == null || !existingPatient.getCpf().equals(dto.getCpf())) &&
            repository.findByCpf(dto.getCpf()).isPresent()) {
            throw new IllegalArgumentException("CPF already registered");
        }

        // Check if email is being changed and if it's already in use (only if provided)
        if (dto.getEmail() != null && !dto.getEmail().isBlank() &&
            (existingPatient.getEmail() == null || !existingPatient.getEmail().equals(dto.getEmail())) &&
            repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Update fields
        existingPatient.setName(dto.getName());
        existingPatient.setCpf(dto.getCpf());
        existingPatient.setEmail(dto.getEmail());
        existingPatient.setPhone(dto.getPhone());
        existingPatient.setBirthDate(dto.getBirthDate());
        existingPatient.setAddress(dto.getAddress());
        existingPatient.setMedicalHistory(dto.getMedicalHistory());

        Patient updated = repository.save(existingPatient);
        return mapper.toResponseDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
