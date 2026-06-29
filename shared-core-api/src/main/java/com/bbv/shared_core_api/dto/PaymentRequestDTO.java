package com.bbv.shared_core_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {
    private String contaOrigem;
    private String contaDestino;
    private BigDecimal valor;
    private String tipoProduto; // Ex: PIX, CARTAO_CREDITO, TED, IMPOSTO
    private String informacaoAdicional;
}
