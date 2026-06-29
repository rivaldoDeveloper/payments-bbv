package com.rivaldo.payment_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
public class BbvIntegrationConfig {

    // 1. Canal Central de Entrada
    @Bean
    public MessageChannel paymentInputChannel() {
        return new DirectChannel();
    }

    // 2. Canais de Destino
    @Bean public MessageChannel pixChannel() { return new DirectChannel(); }
    @Bean public MessageChannel creditCardChannel() { return new DirectChannel(); }
    @Bean public MessageChannel taxChannel() { return new DirectChannel(); }
    @Bean public MessageChannel tedChannel() { return new DirectChannel(); }

    // 3. Roteador Dinâmico por Expressão SpEL (Lê o campo 'tipoProduto' via reflexão interna do Spring)
    @Bean
    public IntegrationFlow paymentRoutingFlow() {
        return IntegrationFlow.from(paymentInputChannel())
                .<Object, String>route(
                        // O Spring lê a propriedade 'tipoProduto' de qualquer objeto que entrar aqui,
                        // pegando o .name() do Enum dinamicamente sem precisar do import da classe!
                        payload -> {
                            try {
                                // Executa uma leitura reflexiva segura do campo
                                java.lang.reflect.Method method = payload.getClass().getMethod("getTipoProduto");
                                Object enumValue = method.invoke(payload);
                                return enumValue != null ? enumValue.toString() : "";
                            } catch (Exception e) {
                                return "";
                            }
                        },
                        mapping -> mapping
                                .channelMapping("PIX", "pixChannel")
                                .channelMapping("CARTAO_CREDITO", "creditCardChannel")
                                .channelMapping("IMPOSTO", "taxChannel")
                                .channelMapping("TED", "tedChannel")
                )
                .get();
    }
}