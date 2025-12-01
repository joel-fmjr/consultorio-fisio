package com.joel.consultorio_fisio.payment;

import com.joel.consultorio_fisio.appointment.Appointment;
import com.joel.consultorio_fisio.appointment.AppointmentRepository;
import com.joel.consultorio_fisio.exception.ResourceNotFoundException;
import com.joel.consultorio_fisio.patient.Patient;
import com.joel.consultorio_fisio.patient.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;
    private PaymentDTO paymentDTO;
    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = Patient.builder()
                .id(1L)
                .name("Test Patient")
                .email("test@example.com")
                .phone("123456789")
                .build();

        payment = Payment.builder()
                .id(1L)
                .mercadoPagoPaymentId(12345L)
                .patient(patient)
                .amount(new BigDecimal("100.00"))
                .status(PaymentStatus.PENDING)
                .paymentMethod("pix")
                .build();

        paymentDTO = PaymentDTO.builder()
                .id(1L)
                .mercadoPagoPaymentId(12345L)
                .patientId(1L)
                .amount(new BigDecimal("100.00"))
                .status(PaymentStatus.PENDING)
                .paymentMethod("pix")
                .build();
    }

    @Test
    void testFindAll() {
        // Given
        List<Payment> payments = Arrays.asList(payment);
        List<PaymentDTO> paymentDTOs = Arrays.asList(paymentDTO);
        when(paymentRepository.findAll()).thenReturn(payments);
        when(paymentMapper.toDTOList(payments)).thenReturn(paymentDTOs);

        // When
        List<PaymentDTO> result = paymentService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentRepository).findAll();
    }

    @Test
    void testFindById_Success() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDTO(payment)).thenReturn(paymentDTO);

        // When
        PaymentDTO result = paymentService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(paymentRepository).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            paymentService.findById(1L);
        });
    }

    @Test
    void testFindByMercadoPagoId_Success() {
        // Given
        when(paymentRepository.findByMercadoPagoPaymentId(12345L)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDTO(payment)).thenReturn(paymentDTO);

        // When
        PaymentDTO result = paymentService.findByMercadoPagoId(12345L);

        // Then
        assertNotNull(result);
        assertEquals(12345L, result.getMercadoPagoPaymentId());
        verify(paymentRepository).findByMercadoPagoPaymentId(12345L);
    }

    @Test
    void testFindByPatientId() {
        // Given
        List<Payment> payments = Arrays.asList(payment);
        List<PaymentDTO> paymentDTOs = Arrays.asList(paymentDTO);
        when(paymentRepository.findByPatientId(1L)).thenReturn(payments);
        when(paymentMapper.toDTOList(payments)).thenReturn(paymentDTOs);

        // When
        List<PaymentDTO> result = paymentService.findByPatientId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentRepository).findByPatientId(1L);
    }

    @Test
    void testFindByStatus() {
        // Given
        List<Payment> payments = Arrays.asList(payment);
        List<PaymentDTO> paymentDTOs = Arrays.asList(paymentDTO);
        when(paymentRepository.findByStatus(PaymentStatus.PENDING)).thenReturn(payments);
        when(paymentMapper.toDTOList(payments)).thenReturn(paymentDTOs);

        // When
        List<PaymentDTO> result = paymentService.findByStatus(PaymentStatus.PENDING);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentRepository).findByStatus(PaymentStatus.PENDING);
    }

    @Test
    void testUpdatePaymentStatus() {
        // Given
        when(paymentRepository.findByMercadoPagoPaymentId(12345L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toDTO(payment)).thenReturn(paymentDTO);

        // When
        PaymentDTO result = paymentService.updatePaymentStatus(12345L, PaymentStatus.APPROVED, "approved");

        // Then
        assertNotNull(result);
        verify(paymentRepository).findByMercadoPagoPaymentId(12345L);
        verify(paymentRepository).save(any(Payment.class));
    }
}
