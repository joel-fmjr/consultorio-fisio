package com.joel.consultorio_fisio.payment;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
@Hidden
@Tag(name = "Webhooks", description = "Endpoints for receiving payment notifications from Mercado Pago")
public class WebhookController {

    private final PaymentService paymentService;

    @PostMapping("/mercadopago")
    @Operation(summary = "Receive Mercado Pago webhook", description = "Endpoint to receive payment status notifications from Mercado Pago")
    public ResponseEntity<Void> handleMercadoPagoWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "x-signature", required = false) String signature,
            @RequestHeader(value = "x-request-id", required = false) String requestId) {

        log.info("Received Mercado Pago webhook: requestId={}, payload={}", requestId, payload);

        try {
            // Extract notification type and data
            String action = (String) payload.get("action");
            Object dataObj = payload.get("data");

            if (dataObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) dataObj;

                if ("payment.created".equals(action) || "payment.updated".equals(action)) {
                    Object idObj = data.get("id");
                    Long paymentId = null;

                    if (idObj instanceof Number) {
                        paymentId = ((Number) idObj).longValue();
                    } else if (idObj instanceof String) {
                        paymentId = Long.valueOf((String) idObj);
                    }

                    if (paymentId != null) {
                        log.info("Processing payment update for Mercado Pago ID: {}", paymentId);
                        paymentService.processWebhookNotification(paymentId);
                    } else {
                        log.warn("Payment ID is null in webhook payload");
                    }
                }
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            // Return 200 anyway to avoid Mercado Pago retries
            return ResponseEntity.ok().build();
        }
    }
}
