package com.example.fraud_detection.repository;

import com.example.fraud_detection.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdOrderByTransactionTimeDesc(Long userId);
}
