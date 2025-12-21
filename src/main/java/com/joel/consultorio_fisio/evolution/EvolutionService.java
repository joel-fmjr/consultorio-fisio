package com.joel.consultorio_fisio.evolution;

import com.joel.consultorio_fisio.evolution.dtos.EvolutionRequestDTO;
import com.joel.consultorio_fisio.evolution.dtos.EvolutionResponseDTO;
import com.joel.consultorio_fisio.exception.ResourceNotFoundException;
import com.joel.consultorio_fisio.patient.Patient;
import com.joel.consultorio_fisio.patient.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvolutionService {

    private final EvolutionRepository evolutionRepository;
    private final EvolutionMapper evolutionMapper;
    private final PatientRepository patientRepository;

    public List<EvolutionResponseDTO> findAll() {
        return evolutionMapper.toResponseDTOList(evolutionRepository.findAll());
    }

    public EvolutionResponseDTO findById(Long id) {
        Evolution evolution = evolutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evolution not found with id: " + id));
        return evolutionMapper.toResponseDTO(evolution);
    }

    public List<EvolutionResponseDTO> findByPatientId(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }

        List<Evolution> evolutions = evolutionRepository.findByPatientIdOrderByEvolutionNumberAsc(patientId);
        return evolutionMapper.toResponseDTOList(evolutions);
    }

    @Transactional
    public EvolutionResponseDTO create(EvolutionRequestDTO dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + dto.getPatientId()));

        Evolution evolution = evolutionMapper.toEntity(dto);
        evolution.setPatient(patient);

        long count = evolutionRepository.countByPatientId(dto.getPatientId());
        evolution.setEvolutionNumber((int) count + 1);

        Evolution saved = evolutionRepository.save(evolution);
        return evolutionMapper.toResponseDTO(saved);
    }

    @Transactional
    public EvolutionResponseDTO update(Long id, EvolutionRequestDTO dto) {
        Evolution existingEvolution = evolutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evolution not found with id: " + id));

        if (!existingEvolution.getPatient().getId().equals(dto.getPatientId())) {
            throw new IllegalArgumentException("Cannot change patient for existing evolution");
        }

        existingEvolution.setConduct(dto.getConduct());

        Evolution updated = evolutionRepository.save(existingEvolution);
        return evolutionMapper.toResponseDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!evolutionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Evolution not found with id: " + id);
        }
        evolutionRepository.deleteById(id);
    }
}
