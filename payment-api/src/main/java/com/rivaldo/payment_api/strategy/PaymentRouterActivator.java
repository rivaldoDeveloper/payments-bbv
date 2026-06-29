package com.rivaldo.payment_api.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PaymentRouterActivator {

    private final Map<String, PaymentStrategy> strategies;

    @Autowired
    public PaymentRouterActivator(List<PaymentStrategy> paymentStrategies) {
        this.strategies = paymentStrategies.stream()
                .collect(Collectors.toMap(PaymentStrategy::getTipoSuportado, strategy -> strategy));
    }

    @ServiceActivator(inputChannel = "pixChannel")
    public void rotearPix(Object transaction) {
        executarEstrategia("PIX", transaction);
    }

    @ServiceActivator(inputChannel = "creditCardChannel")
    public void rotearCartao(Object transaction) {
        executarEstrategia("CARTAO_CREDITO", transaction);
    }

    @ServiceActivator(inputChannel = "tedChannel")
    public void rotearTed(Object transaction) {
        executarEstrategia("TED", transaction);
    }

    @ServiceActivator(inputChannel = "taxChannel")
    public void rotearImposto(Object transaction) {
        executarEstrategia("IMPOSTO", transaction);
    }

    // O método privado agora está escrito corretamente com "C"
    private void executarEstrategia(String tipo, Object transaction) {
        PaymentStrategy strategy = strategies.get(tipo);
        if (strategy != null) {
            strategy.processar(transaction);
        } else {
            // Log amigável caso uma estratégia ainda não tenha sido criada (ex: TED ou Imposto)
            System.out.println("[ROUTER ERROR] Nenhuma classe de estratégia implementada para o tipo: " + tipo);
        }
    }
}