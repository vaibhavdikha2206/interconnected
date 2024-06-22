package com.simplified.interconnected.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "expert_service")
@Data
public class ExpertServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "expert_id", nullable = false)
    private ExpertEntity expert;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;
}

