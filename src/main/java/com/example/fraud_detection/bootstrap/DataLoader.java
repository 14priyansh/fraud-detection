package com.example.fraud_detection.bootstrap;

import com.example.fraud_detection.dto.TransactionRequest;
import com.example.fraud_detection.entity.User;
import com.example.fraud_detection.repository.UserRepository;
import com.example.fraud_detection.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataLoader {
    private final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final UserRepository userRepository;
    private final TransactionService transactionService;

    @Value("${app.demo.enabled:false}")
    private boolean enabled;

    @Value("${app.demo.user-count:5}")
    private int userCount;

    @Value("${app.demo.tx-count:500}")
    private int txCount;

    private final Random rnd = new Random();

    public DataLoader(UserRepository userRepository, TransactionService transactionService) {
        this.userRepository = userRepository;
        this.transactionService = transactionService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadDemoData() {
        if (!enabled) {
            log.info("Demo data disabled (app.demo.enabled=false). Skipping data load.");
            return;
        }

        long existingUsers = userRepository.count();
        if (existingUsers > 0) {
            log.info("Database already contains {} users. Skipping demo data generation.", existingUsers);
            return;
        }

        log.info("Starting demo data generation: users={}, transactions={}", userCount, txCount);

        // sample data
        List<String> locations = Arrays.asList("Delhi", "Mumbai", "London", "Bengaluru", "New York", "San Francisco");
        List<String> devicePrefixes = Arrays.asList("phone", "tablet", "web", "pos");

        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < userCount; i++) {
            User u = new User();
            u.setName("user" + (i + 1));
            String loc = locations.get(rnd.nextInt(locations.size()));
            u.setLocation(loc);
            u.setDeviceId(devicePrefixes.get(rnd.nextInt(devicePrefixes.size())) + "-" + (1000 + i));
            userRepository.save(u);
            userIds.add(u.getId());
        }

        // generate transactions (use TransactionService to apply fraud rules)
        for (int i = 0; i < txCount; i++) {
            Long uid = userIds.get(rnd.nextInt(userIds.size()));
            double amount = generateAmount();
            String location = randomLocation(locations, rnd);
            // sometimes use user's device, sometimes different to create "unusual device" cases
            String device = rnd.nextInt(10) < 7
                    ? "phone-" + (1000 + (uid.intValue() % 10))
                    : "random-" + rnd.nextInt(9999);

            TransactionRequest req = new TransactionRequest();
            req.setUserId(uid);
            req.setAmount(Math.round(amount * 100.0) / 100.0);
            req.setLocation(location);
            // set timestamp to spread across last 48 hours
            req.setTimestamp(LocalDateTime.now().minusMinutes(rnd.nextInt(60 * 48)).toString());
            req.setDeviceId(device);

            try {
                transactionService.createTransaction(req);
            } catch (Exception ex) {
                // don't fail generation on single error
                log.warn("Failed to create demo transaction for user {}: {}", uid, ex.getMessage());
            }
        }

        log.info("Demo data generation completed. Created users: {}, attempted tx: {}", userCount, txCount);
    }

    private double generateAmount() {
        // produce amounts from micro to very large (to hit high-value rule sometimes)
        int r = rnd.nextInt(100);
        if (r < 70) return 10 + rnd.nextDouble() * 200;        // small
        if (r < 90) return 200 + rnd.nextDouble() * 3000;      // medium
        return 10000 + rnd.nextDouble() * 40000;               // high-value occasionally
    }

    private String randomLocation(List<String> list, Random rnd) {
        return list.get(rnd.nextInt(list.size()));
    }
}
