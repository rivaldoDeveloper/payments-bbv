package com.rivaldo.payment_api.controller;

import com.bbv.shared_core_api.domain.enums.ProductType;
import com.bbv.shared_core_api.domain.enums.TransactionStatus;
import com.bbv.shared_core_api.domain.model.Transaction;
import com.rivaldo.payment_api.gateway.BbvPaymentGateway;
import com.rivaldo.payment_api.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bbv.shared_core_api.dto.PaymentRequestDTO;


import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final BbvPaymentGateway paymentGateway;
    private final TransactionRepository transactionRepository;

    @PostMapping
    public ResponseEntity<?> processarPagamento(@RequestBody PaymentRequestDTO request) {
        // Simulando a criação de um código único de transação antes de enviar ao fluxo
        String codigoTransacao = UUID.randomUUID().toString();

        System.out.println("[CONTROLLER] Recebendo requisição de pagamento para o produto: " + request.getTipoProduto());

        Transaction tx = new Transaction();
        tx.setCodigoTransacao(codigoTransacao);
        tx.setContaOrigem(request.getContaOrigem());
        tx.setContaDestino(request.getContaDestino());
        tx.setValor(request.getValor());
        tx.setTipoProduto(ProductType.valueOf(request.getTipoProduto()));
        tx.setStatus(TransactionStatus.valueOf("PENDENTE"));

        // 2. Salva fisicamente no PostgreSQL / H2
        Transaction txSalva = transactionRepository.save(tx);

        // 3. Envia a transação salva para o fluxo do Spring Integration (Router -> Strategies)
        paymentGateway.enviarParaProcessamento(txSalva);

        return ResponseEntity.accepted().body(Map.of(
                "status", tx.getStatus(),
                "codigoTransacao", txSalva.getCodigoTransacao(),
                "mensagem", "Transação registrada com sucesso. Aguardando processamento do lote Batch"
        ));
    }
}
