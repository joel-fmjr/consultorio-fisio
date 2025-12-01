package com.joel.consultorio_fisio.payment;

public enum PaymentStatus {
    PENDING,           // Payment created, waiting for payment
    APPROVED,          // Payment approved
    AUTHORIZED,        // Payment authorized (card)
    IN_PROCESS,        // Payment in process
    IN_MEDIATION,      // Payment in mediation
    REJECTED,          // Payment rejected
    CANCELLED,         // Payment cancelled
    REFUNDED,          // Payment refunded
    CHARGED_BACK       // Payment charged back
}
