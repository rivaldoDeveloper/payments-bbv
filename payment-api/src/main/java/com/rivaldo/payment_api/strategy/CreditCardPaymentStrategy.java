package com.rivaldo.payment_api.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CreditCardPaymentStrategy implements PaymentStrategy {

    private static final Logger log = LoggerFactory.getLogger(CreditCardPaymentStrategy.class);

    // WebClient que adicionamos no seu pom.xml para chamadas HTTP reais para APIs externas
    private final WebClient webClient;

    public CreditCardPaymentStrategy() {
        // Exemplo configurando a URL base para um ambiente de Sandbox/MOCK de pagamentos
        this.webClient = WebClient.builder()
                .baseUrl("https://api.stripe.com/v3") // Simulando a URL de testes da Stripe ou Asaas
                .defaultHeader("Authorization", "Bearer sk_test_sua_chave_falsa_aqui")
                .build();
    }

    @Override
    public void processar(Object transaction) {
        log.info("[STRATEGY - CARTÃO] Ativando checkout via operadora de crédito...");
        log.info("[STRATEGY - CARTÃO] Simulando o uso de cartão de teste (ex: 4242-4242-4242-4242)...");

        // No futuro, aqui faremos o disparo HTTP real usando WebClient:
        // this.webClient.post().uri("/charges").bodyValue(transaction)...

        log.info("[STRATEGY - CARTÃO] Transação Aprovada com sucesso na operadora externa!");
    }

    @Override
    public String getTipoSuportado() {
        return "CARTAO_CREDITO";
    }
}