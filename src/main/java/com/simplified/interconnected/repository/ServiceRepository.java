package com.simplified.interconnected.repository;

import com.simplified.interconnected.models.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
}
