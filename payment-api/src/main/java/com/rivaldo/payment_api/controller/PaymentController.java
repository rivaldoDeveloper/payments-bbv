package com.rivaldo.payment_api.controller;

import com.rivaldo.payment_api.gateway.BbvPaymentGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bbv.shared_core_api.dto.PaymentRequestDTO;


import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final BbvPaymentGateway paymentGateway;

    public PaymentController(BbvPaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    @PostMapping
    public ResponseEntity<?> processarPagamento(@RequestBody PaymentRequestDTO request) {
        // Simulando a criação de um código único de transação antes de enviar ao fluxo
        String codigoTransacao = UUID.randomUUID().toString();

        System.out.println("[CONTROLLER] Recebendo requisição de pagamento para o produto: " + request.getTipoProduto());

        // Injeta o DTO direto no motor do Spring Integration
        // O Router vai ler o método 'getTipoProduto' via reflexão de forma 100% dinâmica!
        paymentGateway.enviarParaProcessamento(request);

        // Retorna sucesso imediato para o cliente (padrão assíncrono/mensageria)
        return ResponseEntity.accepted().body(Map.of(
                "status", "PROCESSANDO",
                "codigoTransacao", codigoTransacao,
                "mensagem", "Pagamento enviado para a fila de processamento do BBVA."
        ));
    }
}
