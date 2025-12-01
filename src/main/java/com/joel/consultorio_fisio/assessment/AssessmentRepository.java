package com.joel.consultorio_fisio.assessment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    List<Assessment> findByPatientIdOrderByAssessmentDateDesc(Long patientId);

    List<Assessment> findByAssessmentDateBetween(LocalDate start, LocalDate end);
}
