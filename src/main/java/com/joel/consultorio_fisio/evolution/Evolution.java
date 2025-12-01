package com.joel.consultorio_fisio.evolution;

import com.joel.consultorio_fisio.patient.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "evolutions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evolution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "evolution_date", nullable = false)
    private LocalDateTime evolutionDate;

    @Column(name = "evolution_number", nullable = false)
    private Integer evolutionNumber;

    @Column(name = "conduct", columnDefinition = "TEXT", nullable = false)
    private String conduct;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (evolutionDate == null) {
            evolutionDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
