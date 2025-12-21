package com.joel.consultorio_fisio.patient;

import com.joel.consultorio_fisio.patient.dtos.PatientRequestDTO;
import com.joel.consultorio_fisio.patient.dtos.PatientResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "API for patient management")
public class PatientController {

    private final PatientService service;

    @GetMapping
    @Operation(summary = "List all patients", description = "Returns a list of all registered patients")
    public ResponseEntity<List<PatientResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find patient by ID", description = "Returns a single patient by their ID")
    public ResponseEntity<PatientResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search patients by name", description = "Returns a list of patients matching the name")
    public ResponseEntity<List<PatientResponseDTO>> findByName(@RequestParam String name) {
        return ResponseEntity.ok(service.findByName(name));
    }

    @PostMapping
    @Operation(summary = "Create a new patient", description = "Creates a new patient record")
    public ResponseEntity<PatientResponseDTO> create(@Valid @RequestBody PatientRequestDTO dto) {
        PatientResponseDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a patient", description = "Updates an existing patient record")
    public ResponseEntity<PatientResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a patient", description = "Deletes a patient record by ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
