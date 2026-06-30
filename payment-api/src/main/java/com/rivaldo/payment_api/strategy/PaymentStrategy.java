package com.rivaldo.payment_api.strategy;

import com.bbv.shared_core_api.domain.model.Transaction;

public interface PaymentStrategy {
    Transaction processar(Object transaction);
    String getTipoSuportado();
}