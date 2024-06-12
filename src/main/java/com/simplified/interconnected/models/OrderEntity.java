package com.simplified.interconnected.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentId;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private Double cost;

    @Column(nullable = false)
    private LocalDateTime orderTimestamp;

    @Column(nullable = false)
    private String paymentStatus;

    @Column(nullable = false)
    private String orderStatus;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @Column(nullable = false)
    private LocalDateTime serviceTimeSlot;
}

