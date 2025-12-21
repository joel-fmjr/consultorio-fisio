package com.joel.consultorio_fisio.patient;

import com.joel.consultorio_fisio.exception.ResourceNotFoundException;
import com.joel.consultorio_fisio.patient.dtos.PatientRequestDTO;
import com.joel.consultorio_fisio.patient.dtos.PatientResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository repository;

    @Mock
    private PatientMapper mapper;

    @InjectMocks
    private PatientService service;

    private PatientRequestDTO patientRequestDTO;
    private PatientResponseDTO patientResponseDTO;
    private Patient patient;

    @BeforeEach
    void setUp() {
        patientRequestDTO = PatientRequestDTO.builder()
                .name("João Silva")
                .cpf("12345678901")
                .email("joao@example.com")
                .phone("11987654321")
                .birthDate(LocalDate.of(1990, 5, 15))
                .address("Rua A, 123")
                .build();

        patientResponseDTO = PatientResponseDTO.builder()
                .id(1L)
                .name("João Silva")
                .cpf("12345678901")
                .email("joao@example.com")
                .phone("11987654321")
                .birthDate(LocalDate.of(1990, 5, 15))
                .address("Rua A, 123")
                .build();

        patient = Patient.builder()
                .id(1L)
                .name("João Silva")
                .cpf("12345678901")
                .email("joao@example.com")
                .phone("11987654321")
                .birthDate(LocalDate.of(1990, 5, 15))
                .address("Rua A, 123")
                .build();
    }

    @Test
    void shouldCreatePatientSuccessfully() {
        when(repository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(mapper.toEntity(any(PatientRequestDTO.class))).thenReturn(patient);
        when(repository.save(any(Patient.class))).thenReturn(patient);
        when(mapper.toResponseDTO(any(Patient.class))).thenReturn(patientResponseDTO);

        PatientResponseDTO result = service.create(patientRequestDTO);

        assertNotNull(result);
        assertEquals("João Silva", result.getName());
        verify(repository).save(any(Patient.class));
    }

    @Test
    void shouldThrowExceptionWhenCpfAlreadyExists() {
        when(repository.findByCpf(anyString())).thenReturn(Optional.of(patient));

        assertThrows(IllegalArgumentException.class, () -> service.create(patientRequestDTO));
        verify(repository, never()).save(any(Patient.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(repository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(repository.findByEmail(anyString())).thenReturn(Optional.of(patient));

        assertThrows(IllegalArgumentException.class, () -> service.create(patientRequestDTO));
        verify(repository, never()).save(any(Patient.class));
    }

    @Test
    void shouldFindPatientById() {
        when(repository.findById(1L)).thenReturn(Optional.of(patient));
        when(mapper.toResponseDTO(any(Patient.class))).thenReturn(patientResponseDTO);

        PatientResponseDTO result = service.findById(1L);

        assertNotNull(result);
        assertEquals("João Silva", result.getName());
    }

    @Test
    void shouldThrowExceptionWhenPatientNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void shouldCreatePatientWithMinimalData() {
        PatientRequestDTO minimalRequestDTO = PatientRequestDTO.builder()
                .name("Quick Patient")
                .phone("11999999999")
                .build();

        PatientResponseDTO minimalResponseDTO = PatientResponseDTO.builder()
                .id(2L)
                .name("Quick Patient")
                .phone("11999999999")
                .build();

        Patient minimalPatient = Patient.builder()
                .id(2L)
                .name("Quick Patient")
                .phone("11999999999")
                .build();

        when(mapper.toEntity(any(PatientRequestDTO.class))).thenReturn(minimalPatient);
        when(repository.save(any(Patient.class))).thenReturn(minimalPatient);
        when(mapper.toResponseDTO(any(Patient.class))).thenReturn(minimalResponseDTO);

        PatientResponseDTO result = service.create(minimalRequestDTO);

        assertNotNull(result);
        assertEquals("Quick Patient", result.getName());
        assertEquals("11999999999", result.getPhone());
        verify(repository).save(any(Patient.class));
    }

    @Test
    void shouldUpdatePatientSuccessfully() {
        when(repository.findById(1L)).thenReturn(Optional.of(patient));
        when(repository.save(any(Patient.class))).thenReturn(patient);
        when(mapper.toResponseDTO(any(Patient.class))).thenReturn(patientResponseDTO);

        PatientResponseDTO result = service.update(1L, patientRequestDTO);

        assertNotNull(result);
        verify(repository).save(any(Patient.class));
    }

    @Test
    void shouldDeletePatientSuccessfully() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentPatient() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void shouldFindAllPatients() {
        when(repository.findAll()).thenReturn(List.of(patient));
        when(mapper.toResponseDTOList(anyList())).thenReturn(List.of(patientResponseDTO));

        List<PatientResponseDTO> result = service.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(repository).findAll();
    }

    @Test
    void shouldSearchPatientsByName() {
        when(repository.findByNameContainingIgnoreCase(anyString())).thenReturn(List.of(patient));
        when(mapper.toResponseDTOList(anyList())).thenReturn(List.of(patientResponseDTO));

        List<PatientResponseDTO> result = service.findByName("João");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(repository).findByNameContainingIgnoreCase("João");
    }
}
