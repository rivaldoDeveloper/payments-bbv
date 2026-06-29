package com.rivaldo.payment_api.repository;


import com.bbv.shared_core_api.domain.enums.TransactionStatus;
import com.bbv.shared_core_api.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByStatus(TransactionStatus status);
}
