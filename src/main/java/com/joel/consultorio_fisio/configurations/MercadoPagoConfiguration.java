package com.joel.consultorio_fisio.configurations;

import com.mercadopago.MercadoPagoConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MercadoPagoConfiguration {

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @PostConstruct
    public void init() {
        if (accessToken == null || accessToken.isEmpty()) {
            log.warn("Mercado Pago access token not configured! Set MP_ACCESS_TOKEN environment variable.");
        } else {
            MercadoPagoConfig.setAccessToken(accessToken);
            log.info("Mercado Pago access token configured successfully");
        }
    }
}
