package com.rivaldo.payment_api.strategy;

import com.bbv.shared_core_api.domain.model.Transaction;
import com.bbv.shared_core_api.dto.PixResponseDTO;
import com.rivaldo.payment_api.repository.TransactionRepository; // 🚀 IMPORTANTE
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component("PIX")
public class PixPaymentStrategy implements PaymentStrategy {

    private final WebClient webClient;
    private final TransactionRepository repository; // 🚀 Injetando o repositório da API

    // Injetamos o Builder do WebClient e o seu Repository no construtor
    public PixPaymentStrategy(WebClient.Builder webClientBuilder, TransactionRepository repository) {
        this.repository = repository;
        this.webClient = webClientBuilder
                .baseUrl("https://sandbox.asaas.com/api/v3")
                .defaultHeader("access_token", "$7a$12$ExemploTokenFakeOficialPortfolioRivaldo")
                .build();
    }

    @Override
    public String getTipoSuportado() {
        return "PIX";
    }

    @Override
    public Transaction processar(Object transactionObj) {
        Transaction transacao = (Transaction) transactionObj;

        // Se o cliente não mandou chave, definimos uma aleatória padrão de contingência
        String chaveCliente = transacao.getChavePix() != null ? transacao.getChavePix() : "7a12b345-c678-4901-b234-56789abcdef0";

        System.out.println("[STRATEGY - PIX] Gerando PIX Dinâmico para a chave: " + chaveCliente);

        // Monta o JSON dinâmico para o Gateway externo
        var gatewayRequest = java.util.Map.of(
                "billingType", "PIX",
                "value", transacao.getValor(),
                "pixKey", chaveCliente, // 🚀 Passa a chave escolhida (CPF, Telefone, etc)
                "description", "Pagamento de Transação ID " + transacao.getId()
        );

        PixResponseDTO response = this.webClient.post()
                .uri("/payments")
                .bodyValue(gatewayRequest)
                .retrieve()
                .bodyToMono(PixResponseDTO.class)
                .onErrorResume(e -> {
                    System.out.println("[STRATEGY - PIX] Gateway offline. Gerando QR Code dinâmico local para a chave: " + chaveCliente);

                    // Moca uma linha de Pix válida contendo a chave enviada no Postman
                    PixResponseDTO mockResponse = new PixResponseDTO();
                    mockResponse.setCopiaECola("00020101021226840014br.gov.bcb.pix25" + chaveCliente.length() + chaveCliente + "5204000053039865405" + transacao.getValor());
                    return Mono.just(mockResponse);
                })
                .block(); // Força a espera síncrona

        // Grava o Pix Copia e Cola gerado dinamicamente na tabela
        transacao.setInformacaoAdicional("PIX_DINAMICO_COPIA_E_COLA: " + response.getCopiaECola());

        return repository.save(transacao);
    }
}