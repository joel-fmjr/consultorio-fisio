package com.joel.consultorio_fisio.payment;

import com.joel.consultorio_fisio.payment.dtos.PaymentResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaymentMapper {

    public PaymentResponseDTO toResponseDTO(Payment entity) {
        if (entity == null) {
            return null;
        }
        return PaymentResponseDTO.builder()
                .id(entity.getId())
                .mercadoPagoPaymentId(entity.getMercadoPagoPaymentId())
                .patientId(entity.getPatient() != null ? entity.getPatient().getId() : null)
                .appointmentId(entity.getAppointment() != null ? entity.getAppointment().getId() : null)
                .amount(entity.getAmount())
                .status(entity.getStatus())
                .paymentMethod(entity.getPaymentMethod())
                .qrCode(entity.getQrCode())
                .qrCodeBase64(entity.getQrCodeBase64())
                .ticketUrl(entity.getTicketUrl())
                .externalReference(entity.getExternalReference())
                .statusDetail(entity.getStatusDetail())
                .description(entity.getDescription())
                .payerEmail(entity.getPayerEmail())
                .dateOfExpiration(entity.getDateOfExpiration())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<PaymentResponseDTO> toResponseDTOList(List<Payment> entities) {
        return entities.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public PIXPaymentResponse toPIXPaymentResponse(Payment entity) {
        if (entity == null) {
            return null;
        }
        return PIXPaymentResponse.builder()
                .id(entity.getId())
                .mercadoPagoPaymentId(entity.getMercadoPagoPaymentId())
                .amount(entity.getAmount())
                .status(entity.getStatus())
                .statusDetail(entity.getStatusDetail())
                .qrCode(entity.getQrCode())
                .qrCodeBase64(entity.getQrCodeBase64())
                .ticketUrl(entity.getTicketUrl())
                .externalReference(entity.getExternalReference())
                .dateOfExpiration(entity.getDateOfExpiration())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
