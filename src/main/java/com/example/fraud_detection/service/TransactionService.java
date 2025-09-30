package com.example.fraud_detection.service;

import com.example.fraud_detection.client.MLClient;
import com.example.fraud_detection.dto.TransactionRequest;
import com.example.fraud_detection.dto.TransactionResponse;
import com.example.fraud_detection.entity.FraudAlert;
import com.example.fraud_detection.entity.Transaction;
import com.example.fraud_detection.entity.User;
import com.example.fraud_detection.repository.FraudAlertRepository;
import com.example.fraud_detection.repository.TransactionRepository;
import com.example.fraud_detection.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final FraudAlertRepository fraudAlertRepository;
    private final MLClient mlClient;   //

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              FraudAlertRepository fraudAlertRepository,
                              MLClient mlClient) {  // Inject MLClient
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.fraudAlertRepository = fraudAlertRepository;
        this.mlClient = mlClient;
    }

    public TransactionResponse createTransaction(TransactionRequest req) {
        User user = null;

        if (req.getUserId() != null) {
            user = userRepository.findById(req.getUserId()).orElse(null);
        }

        if (user == null) {
            User newUser = new User();
            newUser.setName(req.getUserName() != null ? req.getUserName() : "anonymous");
            newUser.setLocation(req.getLocation());
            newUser.setDeviceId(req.getDeviceId());
            user = userRepository.save(newUser);
        }

        // Create transaction
        Transaction tx = new Transaction();
        tx.setUser(user);
        tx.setAmount(req.getAmount());
        LocalDateTime t = (req.getTimestamp() != null)
                ? LocalDateTime.parse(req.getTimestamp())
                : LocalDateTime.now();
        tx.setTransactionTime(t);
        tx.setLocation(req.getLocation());
        tx.setDeviceId(req.getDeviceId());

        tx = transactionRepository.save(tx);

        // Run rule-based fraud checks
        List<String> reasons = new ArrayList<>();
        double score = 0.0;

        if (tx.getAmount() != null && tx.getAmount() > 10000) {
            reasons.add("High value transaction");
            score += 0.6;
        }

        if (user.getDeviceId() != null && !user.getDeviceId().equals(tx.getDeviceId())) {
            reasons.add("Unusual device used");
            score += 0.2;
        }

        if (user.getLocation() != null && !user.getLocation().equalsIgnoreCase(tx.getLocation())) {
            reasons.add("Unusual location");
            score += 0.2;
        }

        List<Transaction> recentTxs = transactionRepository.findByUserIdOrderByTransactionTimeDesc(user.getId());
        long count = recentTxs.stream()
                .filter(tr -> tr.getTransactionTime().isAfter(LocalDateTime.now().minusMinutes(1)))
                .count();
        if (count > 3) {
            reasons.add("Too many transactions in a short time");
            score += 0.3;
        }

        //  Call ML API
        double mlProb = mlClient.getFraudProbability(new double[]{
                tx.getAmount() != null ? tx.getAmount() : 0.0,
                count, // recent transactions in last min
                tx.getLocation() != null ? tx.getLocation().hashCode() % 1000 : 0, // basic encoding
                tx.getDeviceId() != null ? tx.getDeviceId().hashCode() % 1000 : 0
        });

        if (mlProb > 0.5) {
            reasons.add("ML model flagged as suspicious (prob=" + mlProb + ")");
            score += mlProb; // merge ML probability into score
        }

        boolean fraudulent = !reasons.isEmpty();

        if (fraudulent) {
            FraudAlert alert = new FraudAlert();
            alert.setTransaction(tx);
            alert.setReason(String.join(", ", reasons));
            alert.setScore(score);
            fraudAlertRepository.save(alert);
        }

        return new TransactionResponse(tx.getId(), fraudulent, reasons, score);
    }

    public List<Transaction> getTransactionsForUser(Long userId) {
        return transactionRepository.findByUserIdOrderByTransactionTimeDesc(userId);
    }
}
