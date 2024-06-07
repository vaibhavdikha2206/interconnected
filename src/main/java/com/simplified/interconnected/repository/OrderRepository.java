package com.simplified.interconnected.repository;

import com.simplified.interconnected.models.OrderEntity;
import com.simplified.interconnected.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByPaymentId(String paymentId);
}
