package com.campus.portal.controller;

import com.campus.portal.dto.AIStatsDTO;
import com.campus.portal.service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/estimate")
    public ResponseEntity<AIStatsDTO.EstimationResponse> getEstimation(@RequestParam Long categoryId) {
        return ResponseEntity.ok(aiService.getEstimatedTime(categoryId));
    }

    @GetMapping("/performance/departments")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<AIStatsDTO.DepartmentPerformance>> getDepartmentPerformance() {
        return ResponseEntity.ok(aiService.getDepartmentPerformance());
    }

    @GetMapping("/performance/staff")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<AIStatsDTO.StaffPerformance>> getStaffPerformance() {
        return ResponseEntity.ok(aiService.getStaffPerformance());
    }
}
