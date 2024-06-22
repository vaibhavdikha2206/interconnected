package com.simplified.interconnected.repository;

import com.simplified.interconnected.models.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;


public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByPaymentId(String paymentId);

    @Query("SELECT COUNT(o) > 0 FROM OrderEntity o WHERE o.expert.id = :expertId AND o.serviceTimeSlot = :serviceTimeSlot")
    boolean existsByExpertIdAndServiceTimeSlot(@Param("expertId") Long expertId, @Param("serviceTimeSlot") LocalDateTime serviceTimeSlot);
}
