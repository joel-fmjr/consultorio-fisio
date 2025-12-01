package com.joel.consultorio_fisio.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByIsCancelled(Boolean isCancelled);
    List<Appointment> findByPatientIdAndIsCancelled(Long patientId, Boolean isCancelled);
}
