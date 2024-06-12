package com.simplified.interconnected.dto;

import com.simplified.interconnected.models.TimeSlotEntity;
import com.simplified.interconnected.utils.DayOfWeek;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeSlotWithDate {

    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;

    public TimeSlotWithDate(TimeSlotEntity timeSlot, LocalDate date) {
        this.dayOfWeek = timeSlot.getDayOfWeek();
        this.startTime = timeSlot.getStartTime();
        this.endTime = timeSlot.getEndTime();
        this.date = date;
    }

    // Getters and setters
}
