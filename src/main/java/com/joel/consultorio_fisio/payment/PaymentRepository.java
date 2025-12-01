package com.joel.consultorio_fisio.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByMercadoPagoPaymentId(Long mercadoPagoPaymentId);

    List<Payment> findByPatientId(Long patientId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByExternalReference(String externalReference);
}
