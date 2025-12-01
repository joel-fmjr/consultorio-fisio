package com.joel.consultorio_fisio.evolution;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evolutions")
@RequiredArgsConstructor
@Tag(name = "Evolutions", description = "API for patient evolution tracking")
public class EvolutionController {

    private final EvolutionService service;

    @GetMapping
    @Operation(summary = "List all evolutions", description = "Returns a list of all evolution notes")
    public ResponseEntity<List<EvolutionDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find evolution by ID", description = "Returns a single evolution note by its ID")
    public ResponseEntity<EvolutionDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get patient evolution history",
               description = "Returns all evolution notes for a specific patient, ordered by sequence number")
    public ResponseEntity<List<EvolutionDTO>> findByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(service.findByPatientId(patientId));
    }

    @PostMapping
    @Operation(summary = "Create evolution note",
               description = "Creates a new evolution note for a patient. Evolution number is auto-generated.")
    public ResponseEntity<EvolutionDTO> create(@Valid @RequestBody EvolutionDTO dto) {
        EvolutionDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update evolution note",
               description = "Updates an existing evolution note. Patient and evolution number cannot be changed.")
    public ResponseEntity<EvolutionDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody EvolutionDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete evolution note", description = "Deletes an evolution note by ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
