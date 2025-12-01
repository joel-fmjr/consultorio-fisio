package com.joel.consultorio_fisio.assessment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
@Tag(name = "Assessments", description = "API for physiotherapy assessment management")
public class AssessmentController {

    private final AssessmentService service;

    @GetMapping
    @Operation(summary = "Get all assessments", description = "Returns a list of all physiotherapy assessments")
    public ResponseEntity<List<AssessmentDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get assessment by ID", description = "Returns a single assessment by its ID")
    public ResponseEntity<AssessmentDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get assessments by patient ID", description = "Returns all assessments for a specific patient, ordered by date (most recent first)")
    public ResponseEntity<List<AssessmentDTO>> findByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(service.findByPatientId(patientId));
    }

    @PostMapping
    @Operation(summary = "Create new assessment", description = "Creates a new physiotherapy assessment")
    public ResponseEntity<AssessmentDTO> create(@Valid @RequestBody AssessmentDTO dto) {
        AssessmentDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update assessment", description = "Updates an existing assessment")
    public ResponseEntity<AssessmentDTO> update(@PathVariable Long id, @Valid @RequestBody AssessmentDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete assessment", description = "Deletes an assessment by its ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
