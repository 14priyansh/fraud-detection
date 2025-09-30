package com.example.fraud_detection.repository;

import com.example.fraud_detection.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
