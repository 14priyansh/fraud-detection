package com.example.fraud_detection.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@Component
public class MLClient {

    private final RestTemplate restTemplate;
    private final String mlApiUrl = "http://127.0.0.1:8000/predict"; // FastAPI service

    public MLClient() {
        this.restTemplate = new RestTemplate();
    }

    public double getFraudProbability(double[] features) {
        try {
            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("features", features);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Call FastAPI
            ResponseEntity<Map> response = restTemplate.postForEntity(mlApiUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object prob = response.getBody().get("fraud_probability");
                if (prob != null) {
                    return ((Number) prob).doubleValue();
                }
            }
        } catch (Exception e) {
            System.err.println("Error calling ML API: " + e.getMessage());
        }
        return 0.0; // default fallback
    }
}
