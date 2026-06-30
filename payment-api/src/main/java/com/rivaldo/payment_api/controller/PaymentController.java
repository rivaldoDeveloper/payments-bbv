package com.rivaldo.payment_api.controller;

import com.bbv.shared_core_api.domain.enums.ProductType;
import com.bbv.shared_core_api.domain.enums.TransactionStatus;
import com.bbv.shared_core_api.domain.model.Transaction;
import com.rivaldo.payment_api.gateway.BbvPaymentGateway;
import com.rivaldo.payment_api.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bbv.shared_core_api.dto.PaymentRequestDTO;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
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
        String codigoTransacao = UUID.randomUUID().toString();

        System.out.println("[CONTROLLER] Recebendo requisição de pagamento para o produto: " + request.getTipoProduto());

        // 🛡️ VALIDAÇÃO CRUCIAL: Se o Postman mandar dados nulos ou errados, barra aqui antes de quebrar o banco
        if (request.getContaOrigem() == null || request.getContaDestino() == null || request.getValor() == null) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Os campos 'contaOrigem', 'contaDestino' e 'valor' são obrigatórios no JSON."));
        }

        // ==========================================
        // 📄 FLUXO EXCLUSIVO DO BOLETO (RETORNA PDF)
        // ==========================================
        if ("BOLETO".equalsIgnoreCase(request.getTipoProduto())) {
            System.out.println("[CONTROLLER] Desviando para o fluxo síncrono de renderização de PDF do Boleto");

            // Salva no banco de dados para o controle do Batch
            Transaction txBoleto = new Transaction();
            txBoleto.setCodigoTransacao(codigoTransacao);
            txBoleto.setContaOrigem(request.getContaOrigem());
            txBoleto.setContaDestino(request.getContaDestino());
            txBoleto.setValor(request.getValor());
            txBoleto.setTipoProduto(ProductType.valueOf("BOLETO"));
            txBoleto.setStatus(TransactionStatus.valueOf("PENDENTE"));
            txBoleto.setInformacaoAdicional("BOLETO_GERADO_PREVIEW_PDF");
            transactionRepository.save(txBoleto);

            // Gera os bytes do PDF em memória limpa
            byte[] pdfBytes;
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                String linhaDigitavel = "34191.79001 01043.513184 91020.150008 7 97610000015000";

                out.write("%PDF-1.4\n".getBytes(StandardCharsets.UTF_8));
                out.write("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n".getBytes(StandardCharsets.UTF_8));
                out.write("2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n".getBytes(StandardCharsets.UTF_8));
                out.write("3 0 obj\n<< /Type /Page /Parent 2 0 R /Resources << /Font << /F1 << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> >> >> /MediaBox [0 0 595 842] /Contents 4 0 R >>\nendobj\n".getBytes(StandardCharsets.UTF_8));

                String conteudoTexto = "BT /F1 14 Tf 50 750 Td (BANCO BBV S.A. | 341-7) Tj "
                        + "0 -40 Td (LINHA DIGITAVEL: " + linhaDigitavel + ") Tj "
                        + "0 -40 Td (CONTA ORIGEM: " + request.getContaOrigem() + ") Tj "
                        + "0 -30 Td (CONTA DESTINO: " + request.getContaDestino() + ") Tj "
                        + "0 -30 Td (VALOR DO BOLETO: R$ " + request.getValor() + ") Tj ET";

                out.write("4 0 obj\n<< /Length ".getBytes(StandardCharsets.UTF_8));
                out.write(String.valueOf(conteudoTexto.length()).getBytes(StandardCharsets.UTF_8));
                out.write(" >>\nstream\n".getBytes(StandardCharsets.UTF_8));
                out.write(conteudoTexto.getBytes(StandardCharsets.UTF_8));
                out.write("\nendstream\nendobj\n".getBytes(StandardCharsets.UTF_8));
                out.write("xref\n0 5\n0000000000 65535 f \n0000000009 00000 n \n0000000056 00000 n \n0000000111 00000 n \n0000000302 00000 n \ntrailer\n<< /Size 5 /Root 1 0 R >>\nstartxref\n415\n%%EOF".getBytes(StandardCharsets.UTF_8));

                pdfBytes = out.toByteArray();
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }

            // Retorna o PDF diretamente na tela do Postman
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=boleto_" + codigoTransacao + ".pdf")
                    .body(pdfBytes);
        }

        // ==========================================
        // 🔄 FLUXO PADRÃO DO INTEGRATION (PIX / CARD)
        // ==========================================
        Transaction tx = new Transaction();
        tx.setCodigoTransacao(codigoTransacao);
        tx.setContaOrigem(request.getContaOrigem());
        tx.setContaDestino(request.getContaDestino());
        tx.setValor(request.getValor());
        tx.setTipoProduto(ProductType.valueOf(request.getTipoProduto().toUpperCase()));
        tx.setStatus(TransactionStatus.valueOf("PENDENTE"));
        tx.setChavePix(request.getChavePix());

        Transaction txSalva = transactionRepository.save(tx);
        paymentGateway.enviarParaProcessamento(txSalva);

        return ResponseEntity.accepted().body(Map.of(
                "status", tx.getStatus(),
                "codigoTransacao", txSalva.getCodigoTransacao(),
                "mensagem", "Transação registrada com sucesso. Aguardando processamento do lote Batch"
        ));
    }
}