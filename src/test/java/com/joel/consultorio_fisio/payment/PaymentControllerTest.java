package com.joel.consultorio_fisio.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joel.consultorio_fisio.payment.dtos.PaymentResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    private PaymentResponseDTO paymentResponseDTO;
    private PIXPaymentResponse pixPaymentResponse;

    @BeforeEach
    void setUp() {
        paymentResponseDTO = PaymentResponseDTO.builder()
                .id(1L)
                .mercadoPagoPaymentId(12345L)
                .patientId(1L)
                .amount(new BigDecimal("100.00"))
                .status(PaymentStatus.PENDING)
                .paymentMethod("pix")
                .createdAt(LocalDateTime.now())
                .build();

        pixPaymentResponse = PIXPaymentResponse.builder()
                .id(1L)
                .mercadoPagoPaymentId(12345L)
                .amount(new BigDecimal("100.00"))
                .status(PaymentStatus.PENDING)
                .qrCode("00020126580014br.gov.bcb.pix0136test")
                .qrCodeBase64("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==")
                .ticketUrl("https://www.mercadopago.com/mlb/payments/12345/ticket")
                .externalReference("FISIO-test-123")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreatePIXPayment() throws Exception {
        // Given
        PIXPaymentRequest request = PIXPaymentRequest.builder()
                .patientId(1L)
                .amount(new BigDecimal("100.00"))
                .description("Test payment")
                .build();

        when(paymentService.createPIXPayment(any(PIXPaymentRequest.class)))
                .thenReturn(pixPaymentResponse);

        // When & Then
        mockMvc.perform(post("/api/payments/pix")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.qrCode").exists())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testFindById() throws Exception {
        // Given
        when(paymentService.findById(1L)).thenReturn(paymentResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.patientId").value(1));
    }

    @Test
    void testFindByMercadoPagoId() throws Exception {
        // Given
        when(paymentService.findByMercadoPagoId(12345L)).thenReturn(paymentResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/payments/mercadopago/12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mercadoPagoPaymentId").value(12345));
    }

    @Test
    void testFindByPatientId() throws Exception {
        // Given
        List<PaymentResponseDTO> payments = Arrays.asList(paymentResponseDTO);
        when(paymentService.findByPatientId(1L)).thenReturn(payments);

        // When & Then
        mockMvc.perform(get("/api/payments/patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").value(1));
    }

    @Test
    void testFindAll() throws Exception {
        // Given
        List<PaymentResponseDTO> payments = Arrays.asList(paymentResponseDTO);
        when(paymentService.findAll()).thenReturn(payments);

        // When & Then
        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testFindAllByStatus() throws Exception {
        // Given
        List<PaymentResponseDTO> payments = Arrays.asList(paymentResponseDTO);
        when(paymentService.findByStatus(PaymentStatus.PENDING)).thenReturn(payments);

        // When & Then
        mockMvc.perform(get("/api/payments?status=PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }
}
