package com.joel.consultorio_fisio.payment;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaymentMapper {

    public PaymentDTO toDTO(Payment entity) {
        if (entity == null) {
            return null;
        }
        return PaymentDTO.builder()
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

    public Payment toEntity(PaymentDTO dto) {
        if (dto == null) {
            return null;
        }
        return Payment.builder()
                .id(dto.getId())
                .mercadoPagoPaymentId(dto.getMercadoPagoPaymentId())
                .amount(dto.getAmount())
                .status(dto.getStatus())
                .paymentMethod(dto.getPaymentMethod())
                .qrCode(dto.getQrCode())
                .qrCodeBase64(dto.getQrCodeBase64())
                .ticketUrl(dto.getTicketUrl())
                .externalReference(dto.getExternalReference())
                .statusDetail(dto.getStatusDetail())
                .description(dto.getDescription())
                .payerEmail(dto.getPayerEmail())
                .dateOfExpiration(dto.getDateOfExpiration())
                .build();
    }

    public List<PaymentDTO> toDTOList(List<Payment> entities) {
        return entities.stream()
                .map(this::toDTO)
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
