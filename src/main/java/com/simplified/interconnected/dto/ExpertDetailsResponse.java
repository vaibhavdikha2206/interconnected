package com.simplified.interconnected.dto;

import com.simplified.interconnected.models.ServiceEntity;

import java.util.List;

public class ExpertDetailsResponse {

    private List<ServiceEntity> services;
    private List<TimeSlotWithDate> timeSlots;

    public ExpertDetailsResponse(List<ServiceEntity> services, List<TimeSlotWithDate> timeSlots) {
        this.services = services;
        this.timeSlots = timeSlots;
    }

    // Getters and setters
}

