package com.simplified.interconnected.service;

import com.simplified.interconnected.dto.ExpertDetailsResponse;
import com.simplified.interconnected.dto.TimeSlotWithDate;
import com.simplified.interconnected.models.ServiceEntity;
import com.simplified.interconnected.models.TimeSlotEntity;
import com.simplified.interconnected.repository.ExpertServiceRepository;
import com.simplified.interconnected.repository.ExpertTimeSlotRepository;
import com.simplified.interconnected.repository.OrderRepository;
import com.simplified.interconnected.utils.DayOfWeek;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpertService {

    @Autowired
    private ExpertServiceRepository expertServiceRepository;

    @Autowired
    private ExpertTimeSlotRepository expertTimeSlotRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<ServiceEntity> getServicesByExpertId(Long expertId) {
        return expertServiceRepository.findServicesByExpertId(expertId);
    }

    public List<TimeSlotWithDate> getNextAvailableTimeSlotsByExpertId(Long expertId) {
        List<TimeSlotEntity> timeSlots = expertTimeSlotRepository.findAllTimeSlotsByExpertId(expertId);
        LocalDateTime now = LocalDateTime.now();

        List<TimeSlotWithDate> upcomingTimeSlots = new ArrayList<>();

        // Create a sorted map of dayOfWeek to slots
        Map<DayOfWeek, List<TimeSlotEntity>> slotsByDay = timeSlots.stream()
                .collect(Collectors.groupingBy(TimeSlotEntity::getDayOfWeek, TreeMap::new, Collectors.toList()));

        // Check each of the next 14 days (to ensure we get 10 slots)
        for (int i = 0; i < 14 && upcomingTimeSlots.size() < 10; i++) {
            LocalDate date = now.toLocalDate().plusDays(i);

            java.time.DayOfWeek dayOfWeek = date.getDayOfWeek();

            List<TimeSlotEntity> slotsForDay = slotsByDay.getOrDefault(dayOfWeek, Collections.emptyList());
            for (TimeSlotEntity slot : slotsForDay) {
                LocalDateTime slotDateTime = LocalDateTime.of(date, slot.getStartTime());
                if (slotDateTime.isAfter(now)) {
                    upcomingTimeSlots.add(new TimeSlotWithDate(slot, date));
                    if (upcomingTimeSlots.size() == 10) break;
                }
            }
        }

        return upcomingTimeSlots;
    }

    public ExpertDetailsResponse getExpertDetails(Long expertId) {
        List<ServiceEntity> services = getServicesByExpertId(expertId);
        List<TimeSlotWithDate> timeSlots = getNextAvailableTimeSlotsByExpertId(expertId);
        return new ExpertDetailsResponse(services, timeSlots);
    }

    // VALIDATION CODE
    public boolean isValidServiceTimeSlot(Long expertId, LocalDateTime serviceTimeSlot) {
        List<TimeSlotEntity> expertTimeSlots = expertTimeSlotRepository.findAllTimeSlotsByExpertId(expertId);
        // Fix this. Use logs. This might not work.
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(serviceTimeSlot.getDayOfWeek().toString());
        LocalTime startTime = serviceTimeSlot.toLocalTime();

        return expertTimeSlots.stream()
                .anyMatch(slot -> slot.getDayOfWeek() == dayOfWeek &&
                        slot.getStartTime().equals(startTime));
    }

    public boolean isTimeSlotAvailable(Long serviceId, LocalDateTime serviceTimeSlot) {
        return !orderRepository.existsByServiceIdAndServiceTimeSlot(serviceId, serviceTimeSlot);
    }

    public boolean validateOrderRequest(Long expertId, Long serviceId, LocalDateTime serviceTimeSlot) {
        return isValidServiceTimeSlot(expertId, serviceTimeSlot) && isTimeSlotAvailable(serviceId, serviceTimeSlot);
    }
}
