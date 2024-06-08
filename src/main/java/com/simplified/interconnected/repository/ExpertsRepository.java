package com.simplified.interconnected.repository;

import com.simplified.interconnected.models.ExpertEntity;
import com.simplified.interconnected.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpertsRepository extends JpaRepository<ExpertEntity, Long> {
}
