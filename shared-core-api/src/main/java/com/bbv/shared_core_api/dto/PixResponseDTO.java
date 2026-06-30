package com.bbv.shared_core_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PixResponseDTO {

    // --- CAMPOS DO PIX ---
    @JsonProperty("encodedImage")
    private String qrCodeBase64;       // A imagem pura em Base64 (caso o front queira renderizar)

    @JsonProperty("payload")
    private String copiaECola;         // Texto do Pix Copia e Cola

    private String urlQrCode;          // 🚀 Link clicável para abrir a imagem do QR Code no navegador

    // --- CAMPOS DO BOLETO ---
    private String linhaDigitavel;     // Código de barras digitável do boleto
    private String urlBoletoPdf;       // 🚀 Link clicável para abrir o PDF do boleto direto no Postman

    // --- CONTROLE DOS GATEWAYS ---
    private String idTransacaoGateway;
}