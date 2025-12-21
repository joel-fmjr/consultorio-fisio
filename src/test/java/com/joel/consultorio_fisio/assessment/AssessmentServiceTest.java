package com.joel.consultorio_fisio.assessment;

import com.joel.consultorio_fisio.assessment.dtos.AssessmentRequestDTO;
import com.joel.consultorio_fisio.assessment.dtos.AssessmentResponseDTO;
import com.joel.consultorio_fisio.exception.ResourceNotFoundException;
import com.joel.consultorio_fisio.patient.Patient;
import com.joel.consultorio_fisio.patient.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentServiceTest {

    @Mock
    private AssessmentRepository repository;

    @Mock
    private AssessmentMapper mapper;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private AssessmentService service;

    private AssessmentRequestDTO assessmentRequestDTO;
    private AssessmentResponseDTO assessmentResponseDTO;
    private Assessment assessment;
    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = Patient.builder()
                .id(1L)
                .name("Maria Silva")
                .build();

        assessmentRequestDTO = AssessmentRequestDTO.builder()
                .patientId(1L)
                .assessmentDate(LocalDate.now())
                .mainComplaint("Dor lombar crônica")
                .painScale(7)
                .hasDiabetes(false)
                .hasHypertension(false)
                .eatingHabits(EatingHabits.HEALTHY)
                .build();

        assessmentResponseDTO = AssessmentResponseDTO.builder()
                .id(1L)
                .patientId(1L)
                .patientName("Maria Silva")
                .assessmentDate(LocalDate.now())
                .mainComplaint("Dor lombar crônica")
                .painScale(7)
                .hasDiabetes(false)
                .hasHypertension(false)
                .eatingHabits(EatingHabits.HEALTHY)
                .build();

        assessment = Assessment.builder()
                .id(1L)
                .patient(patient)
                .assessmentDate(LocalDate.now())
                .mainComplaint("Dor lombar crônica")
                .painScale(7)
                .hasDiabetes(false)
                .hasHypertension(false)
                .eatingHabits(EatingHabits.HEALTHY)
                .build();
    }

    @Test
    void shouldCreateAssessmentSuccessfully() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(mapper.toEntity(any(AssessmentRequestDTO.class))).thenReturn(assessment);
        when(repository.save(any(Assessment.class))).thenReturn(assessment);
        when(mapper.toResponseDTO(any(Assessment.class))).thenReturn(assessmentResponseDTO);

        AssessmentResponseDTO result = service.create(assessmentRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getPatientId());
        assertEquals("Dor lombar crônica", result.getMainComplaint());
        verify(repository).save(any(Assessment.class));
    }

    @Test
    void shouldThrowExceptionWhenPatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.create(assessmentRequestDTO));
        verify(repository, never()).save(any(Assessment.class));
    }

    @Test
    void shouldFindAssessmentById() {
        when(repository.findById(1L)).thenReturn(Optional.of(assessment));
        when(mapper.toResponseDTO(any(Assessment.class))).thenReturn(assessmentResponseDTO);

        AssessmentResponseDTO result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getPatientId());
        assertEquals("Dor lombar crônica", result.getMainComplaint());
    }

    @Test
    void shouldThrowExceptionWhenAssessmentNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void shouldFindAllAssessments() {
        List<Assessment> assessments = Arrays.asList(assessment);
        List<AssessmentResponseDTO> assessmentDTOs = Arrays.asList(assessmentResponseDTO);

        when(repository.findAll()).thenReturn(assessments);
        when(mapper.toResponseDTOList(assessments)).thenReturn(assessmentDTOs);

        List<AssessmentResponseDTO> result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dor lombar crônica", result.get(0).getMainComplaint());
    }

    @Test
    void shouldFindAssessmentsByPatientId() {
        List<Assessment> assessments = Arrays.asList(assessment);
        List<AssessmentResponseDTO> assessmentDTOs = Arrays.asList(assessmentResponseDTO);

        when(repository.findByPatientIdOrderByAssessmentDateDesc(1L)).thenReturn(assessments);
        when(mapper.toResponseDTOList(assessments)).thenReturn(assessmentDTOs);

        List<AssessmentResponseDTO> result = service.findByPatientId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getPatientId());
    }

    @Test
    void shouldUpdateAssessmentSuccessfully() {
        when(repository.findById(1L)).thenReturn(Optional.of(assessment));
        when(repository.save(any(Assessment.class))).thenReturn(assessment);
        when(mapper.toResponseDTO(any(Assessment.class))).thenReturn(assessmentResponseDTO);

        AssessmentResponseDTO result = service.update(1L, assessmentRequestDTO);

        assertNotNull(result);
        verify(repository).save(any(Assessment.class));
    }

    @Test
    void shouldDeleteAssessmentSuccessfully() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentAssessment() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
        verify(repository, never()).deleteById(1L);
    }
}
