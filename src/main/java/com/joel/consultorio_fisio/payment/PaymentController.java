package com.joel.consultorio_fisio.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "API for payment management with Mercado Pago PIX integration")
public class PaymentController {

    private final PaymentService service;

    @PostMapping("/pix")
    @Operation(summary = "Create PIX payment", description = "Creates a new PIX payment via Mercado Pago and returns QR code")
    public ResponseEntity<PIXPaymentResponse> createPIXPayment(@Valid @RequestBody PIXPaymentRequest request) {
        PIXPaymentResponse response = service.createPIXPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find payment by ID", description = "Returns a single payment by its internal ID")
    public ResponseEntity<PaymentDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/mercadopago/{mercadoPagoId}")
    @Operation(summary = "Find payment by Mercado Pago ID", description = "Returns a single payment by its Mercado Pago payment ID")
    public ResponseEntity<PaymentDTO> findByMercadoPagoId(@PathVariable Long mercadoPagoId) {
        return ResponseEntity.ok(service.findByMercadoPagoId(mercadoPagoId));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Find payments by patient", description = "Returns all payments for a specific patient")
    public ResponseEntity<List<PaymentDTO>> findByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(service.findByPatientId(patientId));
    }

    @GetMapping
    @Operation(summary = "List all payments", description = "Returns a list of all payments, optionally filtered by status")
    public ResponseEntity<List<PaymentDTO>> findAll(@RequestParam(required = false) PaymentStatus status) {
        if (status != null) {
            return ResponseEntity.ok(service.findByStatus(status));
        }
        return ResponseEntity.ok(service.findAll());
    }
}
