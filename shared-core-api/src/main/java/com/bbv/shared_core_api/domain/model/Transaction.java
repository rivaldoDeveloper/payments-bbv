package com.bbv.shared_core_api.domain.model;

import com.bbv.shared_core_api.domain.enums.ProductType;
import com.bbv.shared_core_api.domain.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bbv_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_transacao", nullable = false, unique = true, length = 36)
    private String codigoTransacao;

    @Column(name = "conta_origem", nullable = false, length = 20)
    private String contaOrigem;

    @Column(name = "conta_destino", length = 20)
    private String contaDestino;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_produto", nullable = false, length = 30)
    private ProductType tipoProduto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status;

    @Column(name = "informacao_adicional", columnDefinition = "TEXT")
    private String informacaoAdicional;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @Column(name = "data_transferencia")
    private LocalDateTime dataTransferencia;

    @Column(name = "chave_pix")
    private String chavePix; // Pode receber CPF, Telefone, E-mail ou UUID Aleatório

    // Método executado automaticamente pelo JPA antes de salvar um registro novo
    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }
}
