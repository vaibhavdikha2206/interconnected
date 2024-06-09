package com.simplified.interconnected.repository;

import com.simplified.interconnected.models.ExpertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpertRepository extends JpaRepository<ExpertEntity, Long> {
}
