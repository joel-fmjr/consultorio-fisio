package com.joel.consultorio_fisio.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByCpf(String cpf);
    Optional<Patient> findByEmail(String email);
    List<Patient> findByNameContainingIgnoreCase(String name);
}
