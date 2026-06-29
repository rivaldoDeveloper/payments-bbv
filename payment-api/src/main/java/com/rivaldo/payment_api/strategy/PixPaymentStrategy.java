package com.rivaldo.payment_api.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PixPaymentStrategy implements PaymentStrategy {

    private static final Logger log = LoggerFactory.getLogger(PixPaymentStrategy.class);

    @Override
    public void processar(Object transaction) {
        log.info("[STRATEGY - PIX] Gerando QR Code Dinâmico no Sandbox da API de pagamentos...");
        log.info("[STRATEGY - PIX] Chave Pix BBVA enviada como origem da liquidação.");

        // Simulação do retorno do Payload do Banco Central (EMV Qrcode)
        String qrCodeFake = "00020101021226830014br.gov.bcb.pix2561api.asaas.com/v3/pix/qr/testefake";
        log.info("[STRATEGY - PIX] QR Code gerado para o cliente: {}", qrCodeFake);
    }

    @Override
    public String getTipoSuportado() {
        return "PIX";
    }
}