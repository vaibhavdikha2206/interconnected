package com.simplified.interconnected.models;

import com.simplified.interconnected.utils.DayOfWeek;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "timeslots")
@Data
public class TimeSlotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private java.time.LocalTime startTime;

    private java.time.LocalTime endTime;

}

