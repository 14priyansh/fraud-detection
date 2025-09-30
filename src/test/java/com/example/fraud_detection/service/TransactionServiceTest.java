package com.example.fraud_detection.service;

import com.example.fraud_detection.dto.TransactionRequest;
import com.example.fraud_detection.dto.TransactionResponse;
import com.example.fraud_detection.entity.Transaction;
import com.example.fraud_detection.entity.User;
import com.example.fraud_detection.repository.FraudAlertRepository;
import com.example.fraud_detection.repository.TransactionRepository;
import com.example.fraud_detection.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository txRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private FraudAlertRepository alertRepo;

    @InjectMocks
    private TransactionService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void highValueShouldTriggerFraud() {
        // Given a user in DB
        User user = new User(1L, "Priyanshu", "Delhi", "dev-1");
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        // Mock transaction save
        when(txRepo.save(any(Transaction.class))).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            t.setId(100L);
            return t;
        });

        // No recent txs
        when(txRepo.findByUserIdOrderByTransactionTimeDesc(1L)).thenReturn(Collections.emptyList());

        // When a high-value transaction
        TransactionRequest req = new TransactionRequest();
        req.setUserId(1L);
        req.setAmount(20000.0);
        req.setLocation("Delhi");
        req.setDeviceId("dev-1");

        TransactionResponse resp = service.createTransaction(req);

        // Then fraud should be detected
        assertTrue(resp.isFraudulent());
        assertTrue(resp.getReasons().contains("High value transaction"));
        verify(alertRepo, times(1)).save(any());
    }

    @Test
    void normalTransactionShouldNotBeFraud() {
        // Given a normal user
        User user = new User(2L, "TestUser", "Mumbai", "dev-2");
        when(userRepo.findById(2L)).thenReturn(Optional.of(user));

        // Mock tx save
        when(txRepo.save(any(Transaction.class))).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            t.setId(101L);
            return t;
        });

        // No recent txs
        when(txRepo.findByUserIdOrderByTransactionTimeDesc(2L)).thenReturn(Collections.emptyList());

        // When a normal tx
        TransactionRequest req = new TransactionRequest();
        req.setUserId(2L);
        req.setAmount(500.0);
        req.setLocation("Mumbai");
        req.setDeviceId("dev-2");

        TransactionResponse resp = service.createTransaction(req);

        // Then no fraud
        assertFalse(resp.isFraudulent());
        verify(alertRepo, never()).save(any());
    }
}
