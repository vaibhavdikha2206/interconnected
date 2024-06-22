package com.simplified.interconnected.repository;

import com.simplified.interconnected.models.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ExpertServiceRepository extends JpaRepository<ServiceEntity, Long> {

    @Query("SELECT es.service FROM ExpertServiceEntity es WHERE es.expert.id = :expertId")
    List<ServiceEntity> findServicesByExpertId(@Param("expertId") Long expertId);
}

