package com.rivaldo.payment_api.strategy;

import com.bbv.shared_core_api.domain.model.Transaction; // 🚀 Faltava importar a entidade do seu Core
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component("CREDIT_CARD") // 🚀 Nomeamos o componente para bater com o padrão de mercado
public class CreditCardPaymentStrategy implements PaymentStrategy {

    private static final Logger log = LoggerFactory.getLogger(CreditCardPaymentStrategy.class);
    private final WebClient webClient;

    public CreditCardPaymentStrategy() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.stripe.com/v1") // Stripe usa v1 para a API principal
                .defaultHeader("Authorization", "Bearer sk_test_sua_chave_falsa_aqui")
                .build();
    }

    @Override
    public String getTipoSuportado() {
        return "CREDIT_CARD"; // 🚀 Alinhado com o padrão de nomenclatura internacional que costuma usar em Enums
    }

    @Override
    public Transaction processar(Object transactionObj) {
        // 🚀 1. Validação e Cast obrigatório (Igual fizemos no Pix)
        if (!(transactionObj instanceof Transaction)) {
            throw new IllegalArgumentException("O objeto de transação é inválido para a estratégia de Cartão de Crédito");
        }

        Transaction transacao = (Transaction) transactionObj;

        log.info("[STRATEGY - CARTÃO] Ativando checkout para a transação ID: {}", transacao.getId());
        log.info("[STRATEGY - CARTÃO] Enviando valor de R$ {} para aprovação na Stripe...", transacao.getValor());

        // No futuro, o WebClient fará o POST real aqui:
        // this.webClient.post().uri("/charges").bodyValue(...);

        log.info("[STRATEGY - CARTÃO] Transação aprovada com sucesso na operadora externa!");
        transacao.setInformacaoAdicional("CARTAO_APROVADO_STRIPE_GATEWAY");

        // 🚀 AQUI ESTÁ A CORREÇÃO: Faltava retornar o objeto atualizado!
        return transacao;
    }
}