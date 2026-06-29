package com.rivaldo.payment_api.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface BbvPaymentGateway {

    // Posta o objeto genérico enviado pelo controller direto no início do fluxo
    @Gateway(requestChannel = "paymentInputChannel")
    void enviarParaProcessamento(Object transaction);
}