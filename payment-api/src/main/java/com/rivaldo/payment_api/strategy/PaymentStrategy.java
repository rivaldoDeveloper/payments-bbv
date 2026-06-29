package com.rivaldo.payment_api.strategy;

public interface PaymentStrategy {
    void processar(Object transaction);
    String getTipoSuportado();
}