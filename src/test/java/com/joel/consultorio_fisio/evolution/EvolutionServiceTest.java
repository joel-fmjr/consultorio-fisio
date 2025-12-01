package com.joel.consultorio_fisio.evolution;

import com.joel.consultorio_fisio.exception.ResourceNotFoundException;
import com.joel.consultorio_fisio.patient.Patient;
import com.joel.consultorio_fisio.patient.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvolutionServiceTest {

    @Mock
    private EvolutionRepository evolutionRepository;

    @Mock
    private EvolutionMapper evolutionMapper;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private EvolutionService service;

    private Patient patient;
    private EvolutionDTO evolutionDTO;
    private Evolution evolution;

    @BeforeEach
    void setUp() {
        patient = Patient.builder()
                .id(1L)
                .name("João Silva")
                .build();

        evolutionDTO = EvolutionDTO.builder()
                .patientId(1L)
                .evolutionDate(LocalDateTime.of(2025, 12, 1, 10, 0))
                .conduct("Mobilização articular do ombro direito, 30 minutos")
                .build();

        evolution = Evolution.builder()
                .id(1L)
                .patient(patient)
                .evolutionDate(LocalDateTime.of(2025, 12, 1, 10, 0))
                .evolutionNumber(1)
                .conduct("Mobilização articular do ombro direito, 30 minutos")
                .build();
    }

    @Test
    void shouldCreateEvolutionSuccessfully() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(evolutionMapper.toEntity(any(EvolutionDTO.class))).thenReturn(evolution);
        when(evolutionRepository.countByPatientId(1L)).thenReturn(0L);
        when(evolutionRepository.save(any(Evolution.class))).thenReturn(evolution);
        when(evolutionMapper.toDTO(any(Evolution.class))).thenReturn(evolutionDTO);

        EvolutionDTO result = service.create(evolutionDTO);

        assertNotNull(result);
        verify(evolutionRepository).countByPatientId(1L);
        verify(evolutionRepository).save(any(Evolution.class));
    }

    @Test
    void shouldCreateFirstEvolutionWithNumberOne() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(evolutionMapper.toEntity(any(EvolutionDTO.class))).thenReturn(evolution);
        when(evolutionRepository.countByPatientId(1L)).thenReturn(0L);
        when(evolutionRepository.save(any(Evolution.class))).thenAnswer(invocation -> {
            Evolution saved = invocation.getArgument(0);
            assertEquals(1, saved.getEvolutionNumber());
            return saved;
        });
        when(evolutionMapper.toDTO(any(Evolution.class))).thenReturn(evolutionDTO);

        service.create(evolutionDTO);

        verify(evolutionRepository).countByPatientId(1L);
    }

    @Test
    void shouldCreateSecondEvolutionWithNumberTwo() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(evolutionMapper.toEntity(any(EvolutionDTO.class))).thenReturn(evolution);
        when(evolutionRepository.countByPatientId(1L)).thenReturn(1L);
        when(evolutionRepository.save(any(Evolution.class))).thenAnswer(invocation -> {
            Evolution saved = invocation.getArgument(0);
            assertEquals(2, saved.getEvolutionNumber());
            return saved;
        });
        when(evolutionMapper.toDTO(any(Evolution.class))).thenReturn(evolutionDTO);

        service.create(evolutionDTO);

        verify(evolutionRepository).countByPatientId(1L);
    }

    @Test
    void shouldThrowExceptionWhenPatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(evolutionDTO));
        verify(evolutionRepository, never()).save(any(Evolution.class));
    }

    @Test
    void shouldFindEvolutionById() {
        when(evolutionRepository.findById(1L)).thenReturn(Optional.of(evolution));
        when(evolutionMapper.toDTO(any(Evolution.class))).thenReturn(evolutionDTO);

        EvolutionDTO result = service.findById(1L);

        assertNotNull(result);
        verify(evolutionRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenEvolutionNotFound() {
        when(evolutionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void shouldFindEvolutionsByPatientId() {
        Evolution evolution2 = Evolution.builder()
                .id(2L)
                .patient(patient)
                .evolutionDate(LocalDateTime.of(2025, 12, 3, 14, 30))
                .evolutionNumber(2)
                .conduct("Fortalecimento muscular")
                .build();

        when(patientRepository.existsById(1L)).thenReturn(true);
        when(evolutionRepository.findByPatientIdOrderByEvolutionNumberAsc(1L))
                .thenReturn(List.of(evolution, evolution2));
        when(evolutionMapper.toDTOList(anyList())).thenReturn(List.of(evolutionDTO));

        List<EvolutionDTO> result = service.findByPatientId(1L);

        assertNotNull(result);
        verify(evolutionRepository).findByPatientIdOrderByEvolutionNumberAsc(1L);
    }

    @Test
    void shouldThrowExceptionWhenFindingEvolutionsByNonExistentPatient() {
        when(patientRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.findByPatientId(1L));
        verify(evolutionRepository, never()).findByPatientIdOrderByEvolutionNumberAsc(anyLong());
    }

    @Test
    void shouldUpdateEvolutionSuccessfully() {
        EvolutionDTO updateDTO = EvolutionDTO.builder()
                .patientId(1L)
                .conduct("Mobilização articular do ombro direito, 30 minutos + alongamento")
                .build();

        when(evolutionRepository.findById(1L)).thenReturn(Optional.of(evolution));
        when(evolutionRepository.save(any(Evolution.class))).thenReturn(evolution);
        when(evolutionMapper.toDTO(any(Evolution.class))).thenReturn(updateDTO);

        EvolutionDTO result = service.update(1L, updateDTO);

        assertNotNull(result);
        verify(evolutionRepository).save(any(Evolution.class));
    }

    @Test
    void shouldThrowExceptionWhenChangingPatientOnUpdate() {
        EvolutionDTO updateDTO = EvolutionDTO.builder()
                .patientId(2L)
                .conduct("New conduct")
                .build();

        when(evolutionRepository.findById(1L)).thenReturn(Optional.of(evolution));

        assertThrows(IllegalArgumentException.class, () -> service.update(1L, updateDTO));
        verify(evolutionRepository, never()).save(any(Evolution.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentEvolution() {
        when(evolutionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, evolutionDTO));
        verify(evolutionRepository, never()).save(any(Evolution.class));
    }

    @Test
    void shouldDeleteEvolutionSuccessfully() {
        when(evolutionRepository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(evolutionRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentEvolution() {
        when(evolutionRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
        verify(evolutionRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldFindAllEvolutions() {
        when(evolutionRepository.findAll()).thenReturn(List.of(evolution));
        when(evolutionMapper.toDTOList(anyList())).thenReturn(List.of(evolutionDTO));

        List<EvolutionDTO> result = service.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(evolutionRepository).findAll();
    }

    @Test
    void shouldUpdateEvolutionDateWhenProvided() {
        LocalDateTime newDate = LocalDateTime.of(2025, 12, 5, 15, 0);
        EvolutionDTO updateDTO = EvolutionDTO.builder()
                .patientId(1L)
                .evolutionDate(newDate)
                .conduct("Updated conduct")
                .build();

        when(evolutionRepository.findById(1L)).thenReturn(Optional.of(evolution));
        when(evolutionRepository.save(any(Evolution.class))).thenAnswer(invocation -> {
            Evolution saved = invocation.getArgument(0);
            assertEquals(newDate, saved.getEvolutionDate());
            return saved;
        });
        when(evolutionMapper.toDTO(any(Evolution.class))).thenReturn(updateDTO);

        service.update(1L, updateDTO);

        verify(evolutionRepository).save(any(Evolution.class));
    }
}
