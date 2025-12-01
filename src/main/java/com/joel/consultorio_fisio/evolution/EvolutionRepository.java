package com.joel.consultorio_fisio.evolution;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvolutionRepository extends JpaRepository<Evolution, Long> {

    List<Evolution> findByPatientIdOrderByEvolutionNumberAsc(Long patientId);

    Optional<Evolution> findTopByPatientIdOrderByEvolutionNumberDesc(Long patientId);

    long countByPatientId(Long patientId);
}
