package com.simplified.interconnected.repository;

import com.simplified.interconnected.models.TimeSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpertTimeSlotRepository extends JpaRepository<TimeSlotEntity, Long> {

    @Query("SELECT mt.timeslot FROM MasseurTimeSlot mt WHERE mt.masseur.masseurId = :expertId ORDER BY mt.timeSlot.dayOfWeek, mt.timeSlot.startTime")
    List<TimeSlotEntity> findAllTimeSlotsByExpertId(@Param("expertId") Long expertId);
}
