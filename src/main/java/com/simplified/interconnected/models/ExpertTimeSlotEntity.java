package com.simplified.interconnected.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "expert_timeslot")
@Data
public class ExpertTimeSlotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "expert_id", nullable = false)
    private ExpertEntity expert;

    @ManyToOne
    @JoinColumn(name = "timeslot_id", nullable = false)
    private TimeSlotEntity timeSlot;
}

