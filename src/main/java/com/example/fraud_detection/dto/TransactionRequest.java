package com.example.fraud_detection.dto;

import lombok.Data;

@Data
public class TransactionRequest {
    private Long userId;        // Optional: if null, new user will be created
    private String userName;
    private Double amount;
    private String timestamp;   // e.g. "2025-09-28T13:20:00"
    private String location;
    private String deviceId;
}
