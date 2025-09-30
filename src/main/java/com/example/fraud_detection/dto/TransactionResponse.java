package com.example.fraud_detection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class TransactionResponse {
    private Long transactionId;
    private boolean fraudulent;
    private List<String> reasons;
    private double score;
}
