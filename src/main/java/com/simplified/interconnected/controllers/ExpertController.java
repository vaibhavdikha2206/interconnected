package com.simplified.interconnected.controllers;

import com.simplified.interconnected.dto.ExpertDetailsResponse;
import com.simplified.interconnected.models.ExpertEntity;
import com.simplified.interconnected.service.ExpertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/experts")
public class ExpertController {

    @Autowired
    private ExpertService expertService;

    @GetMapping("/list")
    public List<ExpertEntity> getAllExperts() {
        return expertService.getAllExperts();
    }

    @GetMapping("/{expertId}/details")
    public ResponseEntity<ExpertDetailsResponse> getExpertDetails(@PathVariable Long expertId) {
        try {
            ExpertDetailsResponse response = expertService.getExpertDetails(expertId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
