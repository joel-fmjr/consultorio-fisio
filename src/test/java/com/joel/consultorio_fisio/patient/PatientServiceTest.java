package com.joel.consultorio_fisio.patient;

import com.joel.consultorio_fisio.exception.ResourceNotFoundException;
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

    private PatientDTO patientDTO;
    private Patient patient;

    @BeforeEach
    void setUp() {
        patientDTO = PatientDTO.builder()
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
        when(mapper.toEntity(any(PatientDTO.class))).thenReturn(patient);
        when(repository.save(any(Patient.class))).thenReturn(patient);
        when(mapper.toDTO(any(Patient.class))).thenReturn(patientDTO);

        PatientDTO result = service.create(patientDTO);

        assertNotNull(result);
        assertEquals("João Silva", result.getName());
        verify(repository).save(any(Patient.class));
    }

    @Test
    void shouldThrowExceptionWhenCpfAlreadyExists() {
        when(repository.findByCpf(anyString())).thenReturn(Optional.of(patient));

        assertThrows(IllegalArgumentException.class, () -> service.create(patientDTO));
        verify(repository, never()).save(any(Patient.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(repository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(repository.findByEmail(anyString())).thenReturn(Optional.of(patient));

        assertThrows(IllegalArgumentException.class, () -> service.create(patientDTO));
        verify(repository, never()).save(any(Patient.class));
    }

    @Test
    void shouldFindPatientById() {
        when(repository.findById(1L)).thenReturn(Optional.of(patient));
        when(mapper.toDTO(any(Patient.class))).thenReturn(patientDTO);

        PatientDTO result = service.findById(1L);

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
        PatientDTO minimalDTO = PatientDTO.builder()
                .name("Quick Patient")
                .phone("11999999999")
                .build();

        Patient minimalPatient = Patient.builder()
                .id(2L)
                .name("Quick Patient")
                .phone("11999999999")
                .build();

        when(mapper.toEntity(any(PatientDTO.class))).thenReturn(minimalPatient);
        when(repository.save(any(Patient.class))).thenReturn(minimalPatient);
        when(mapper.toDTO(any(Patient.class))).thenReturn(minimalDTO);

        PatientDTO result = service.create(minimalDTO);

        assertNotNull(result);
        assertEquals("Quick Patient", result.getName());
        assertEquals("11999999999", result.getPhone());
        verify(repository).save(any(Patient.class));
    }

    @Test
    void shouldUpdatePatientSuccessfully() {
        when(repository.findById(1L)).thenReturn(Optional.of(patient));
        when(repository.save(any(Patient.class))).thenReturn(patient);
        when(mapper.toDTO(any(Patient.class))).thenReturn(patientDTO);

        PatientDTO result = service.update(1L, patientDTO);

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
        when(mapper.toDTOList(anyList())).thenReturn(List.of(patientDTO));

        List<PatientDTO> result = service.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(repository).findAll();
    }

    @Test
    void shouldSearchPatientsByName() {
        when(repository.findByNameContainingIgnoreCase(anyString())).thenReturn(List.of(patient));
        when(mapper.toDTOList(anyList())).thenReturn(List.of(patientDTO));

        List<PatientDTO> result = service.findByName("João");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(repository).findByNameContainingIgnoreCase("João");
    }
}
