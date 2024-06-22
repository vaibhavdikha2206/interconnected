package com.simplified.interconnected.dto;

import com.simplified.interconnected.models.ExpertEntity;
import com.simplified.interconnected.models.ServiceEntity;
import lombok.Data;

import java.util.List;

@Data
public class ExpertDetailsResponse {

    private ExpertEntity expert;
    private List<TimeSlotWithDate> timeSlots;

    public ExpertDetailsResponse(ExpertEntity expert, List<TimeSlotWithDate> timeSlots) {
        this.expert = expert;
        this.timeSlots = timeSlots;
    }

    // Getters and setters
}

