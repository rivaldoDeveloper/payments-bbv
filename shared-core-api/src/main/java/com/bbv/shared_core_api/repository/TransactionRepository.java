package com.bbv.shared_core_api.repository;

import com.bbv.shared_core_api.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
