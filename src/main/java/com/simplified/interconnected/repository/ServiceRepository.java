package com.simplified.interconnected.repository;

import com.simplified.interconnected.models.Role;
import com.simplified.interconnected.models.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
}
