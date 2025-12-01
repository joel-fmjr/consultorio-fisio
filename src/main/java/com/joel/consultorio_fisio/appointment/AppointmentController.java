package com.joel.consultorio_fisio.appointment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "API for appointment management")
public class AppointmentController {

    private final AppointmentService service;

    @GetMapping
    @Operation(summary = "List all appointments", description = "Returns a list of all appointments")
    public ResponseEntity<List<AppointmentDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find appointment by ID", description = "Returns a single appointment by its ID")
    public ResponseEntity<AppointmentDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Find appointments by patient ID", description = "Returns all appointments for a specific patient")
    public ResponseEntity<List<AppointmentDTO>> findByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(service.findByPatientId(patientId));
    }

    @PostMapping
    @Operation(summary = "Create a new appointment", description = "Creates a new appointment record")
    public ResponseEntity<AppointmentDTO> create(@Valid @RequestBody AppointmentDTO dto) {
        AppointmentDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an appointment", description = "Updates an existing appointment record")
    public ResponseEntity<AppointmentDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an appointment", description = "Deletes an appointment record by ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/pay")
    @Operation(summary = "Mark appointment as paid", description = "Updates the payment status of an appointment to paid")
    public ResponseEntity<AppointmentDTO> markAsPaid(@PathVariable Long id) {
        return ResponseEntity.ok(service.markAsPaid(id));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel appointment", description = "Cancels an appointment by setting its status to cancelled")
    public ResponseEntity<AppointmentDTO> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelAppointment(id));
    }
}
