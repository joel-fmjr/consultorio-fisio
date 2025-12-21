package com.joel.consultorio_fisio.appointment;

import com.joel.consultorio_fisio.appointment.dtos.AppointmentRequestDTO;
import com.joel.consultorio_fisio.appointment.dtos.AppointmentResponseDTO;
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
import java.util.Optional;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository repository;

    @Mock
    private AppointmentMapper mapper;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private AppointmentService service;

    private AppointmentRequestDTO appointmentRequestDTO;
    private AppointmentResponseDTO appointmentResponseDTO;
    private Appointment appointment;
    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = Patient.builder()
                .id(1L)
                .name("João Silva")
                .build();

        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);

        appointmentRequestDTO = AppointmentRequestDTO.builder()
                .patientId(1L)
                .startTime(futureTime)
                .duration(AppointmentDuration.ONE_HOUR)
                .isPaid(false)
                .isCancelled(false)
                .notes("Initial consultation")
                .build();

        appointmentResponseDTO = AppointmentResponseDTO.builder()
                .id(1L)
                .patientId(1L)
                .patientName("João Silva")
                .startTime(futureTime)
                .endTime(futureTime.plusMinutes(60))
                .duration(AppointmentDuration.ONE_HOUR)
                .isPaid(false)
                .isCancelled(false)
                .notes("Initial consultation")
                .build();

        appointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .startTime(futureTime)
                .endTime(futureTime.plusMinutes(60))
                .duration(AppointmentDuration.ONE_HOUR)
                .isPaid(false)
                .isCancelled(false)
                .notes("Initial consultation")
                .build();
    }

    @Test
    void shouldCreateAppointmentSuccessfully() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(mapper.toEntity(any(AppointmentRequestDTO.class))).thenReturn(appointment);
        when(repository.save(any(Appointment.class))).thenReturn(appointment);
        when(mapper.toResponseDTO(any(Appointment.class))).thenReturn(appointmentResponseDTO);

        AppointmentResponseDTO result = service.create(appointmentRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getPatientId());
        verify(repository).save(any(Appointment.class));
    }

    @Test
    void shouldThrowExceptionWhenPatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.create(appointmentRequestDTO));
        verify(repository, never()).save(any(Appointment.class));
    }

    @Test
    void shouldFindAppointmentById() {
        when(repository.findById(1L)).thenReturn(Optional.of(appointment));
        when(mapper.toResponseDTO(any(Appointment.class))).thenReturn(appointmentResponseDTO);

        AppointmentResponseDTO result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getPatientId());
    }

    @Test
    void shouldThrowExceptionWhenAppointmentNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void shouldUpdateAppointmentSuccessfully() {
        when(repository.findById(1L)).thenReturn(Optional.of(appointment));
        when(repository.save(any(Appointment.class))).thenReturn(appointment);
        when(mapper.toResponseDTO(any(Appointment.class))).thenReturn(appointmentResponseDTO);

        AppointmentResponseDTO result = service.update(1L, appointmentRequestDTO);

        assertNotNull(result);
        verify(repository).save(any(Appointment.class));
    }

    @Test
    void shouldMarkAppointmentAsPaid() {
        when(repository.findById(1L)).thenReturn(Optional.of(appointment));
        when(repository.save(any(Appointment.class))).thenReturn(appointment);
        when(mapper.toResponseDTO(any(Appointment.class))).thenReturn(appointmentResponseDTO);

        AppointmentResponseDTO result = service.markAsPaid(1L);

        assertNotNull(result);
        verify(repository).save(any(Appointment.class));
    }

    @Test
    void shouldCancelAppointment() {
        when(repository.findById(1L)).thenReturn(Optional.of(appointment));
        when(repository.save(any(Appointment.class))).thenReturn(appointment);
        when(mapper.toResponseDTO(any(Appointment.class))).thenReturn(appointmentResponseDTO);

        AppointmentResponseDTO result = service.cancelAppointment(1L);

        assertNotNull(result);
        verify(repository).save(any(Appointment.class));
    }

    @Test
    void shouldFindAppointmentsByPatientId() {
        when(repository.findByPatientId(1L)).thenReturn(List.of(appointment));
        when(mapper.toResponseDTOList(anyList())).thenReturn(List.of(appointmentResponseDTO));

        List<AppointmentResponseDTO> result = service.findByPatientId(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(repository).findByPatientId(1L);
    }

    @Test
    void shouldDeleteAppointmentSuccessfully() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentAppointment() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void shouldFindAllAppointments() {
        when(repository.findAll()).thenReturn(List.of(appointment));
        when(mapper.toResponseDTOList(anyList())).thenReturn(List.of(appointmentResponseDTO));

        List<AppointmentResponseDTO> result = service.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(repository).findAll();
    }
}
