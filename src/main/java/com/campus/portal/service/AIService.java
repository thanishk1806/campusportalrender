package com.campus.portal.service;

import com.campus.portal.dto.AIStatsDTO;
import com.campus.portal.entity.*;
import com.campus.portal.repository.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIService {

    private final ComplaintRepository complaintRepository;
    private final DepartmentRepository departmentRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public AIService(ComplaintRepository complaintRepository, 
                      DepartmentRepository departmentRepository,
                      CategoryRepository categoryRepository,
                      UserRepository userRepository) {
        this.complaintRepository = complaintRepository;
        this.departmentRepository = departmentRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public AIStatsDTO.EstimationResponse getEstimatedTime(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        List<Complaint> resolved = complaintRepository.findByCategoryIdAndStatus(categoryId, ComplaintStatus.RESOLVED);
        
        AIStatsDTO.EstimationResponse response = new AIStatsDTO.EstimationResponse();
        response.setCategoryName(category != null ? category.getName() : "Unknown");

        if (resolved.isEmpty()) {
            response.setEstimatedTime("No data yet");
            response.setAverageMinutes(0);
            return response;
        }

        long totalMinutes = 0;
        int count = 0;
        for (Complaint c : resolved) {
            if (c.getResolvedAt() != null && c.getCreatedAt() != null) {
                totalMinutes += Duration.between(c.getCreatedAt(), c.getResolvedAt()).toMinutes();
                count++;
            }
        }

        if (count == 0) {
            response.setEstimatedTime("No data yet");
            response.setAverageMinutes(0);
            return response;
        }

        long avgMinutes = totalMinutes / count;
        response.setAverageMinutes(avgMinutes);
        response.setEstimatedTime(formatDuration(avgMinutes));
        return response;
    }

    public List<AIStatsDTO.DepartmentPerformance> getDepartmentPerformance() {
        List<Department> departments = departmentRepository.findAll();
        List<AIStatsDTO.DepartmentPerformance> performances = new ArrayList<>();

        for (Department dept : departments) {
            List<Complaint> resolved = complaintRepository.findByStatus(ComplaintStatus.RESOLVED).stream()
                .filter(c -> c.getDepartment() != null && c.getDepartment().getId().equals(dept.getId()))
                .collect(Collectors.toList());

            AIStatsDTO.DepartmentPerformance p = new AIStatsDTO.DepartmentPerformance();
            p.setDepartmentName(dept.getName());
            p.setTotalResolved(resolved.size());

            if (!resolved.isEmpty()) {
                long totalMinutes = 0;
                int count = 0;
                for (Complaint c : resolved) {
                    if (c.getResolvedAt() != null && c.getCreatedAt() != null) {
                        totalMinutes += Duration.between(c.getCreatedAt(), c.getResolvedAt()).toMinutes();
                        count++;
                    }
                }
                if (count > 0) {
                    p.setAverageResolutionTimeHours((double) (totalMinutes / count) / 60.0);
                }
            }
            performances.add(p);
        }
        return performances;
    }

    public List<AIStatsDTO.StaffPerformance> getStaffPerformance() {
        List<User> staffMembers = userRepository.findByRoleName(RoleName.ROLE_STAFF);
        List<AIStatsDTO.StaffPerformance> performances = new ArrayList<>();

        for (User staff : staffMembers) {
            List<Complaint> resolved = complaintRepository.findByAssignedStaffIdOrderByCreatedAtDesc(staff.getId()).stream()
                .filter(c -> c.getStatus() == ComplaintStatus.RESOLVED)
                .collect(Collectors.toList());

            AIStatsDTO.StaffPerformance p = new AIStatsDTO.StaffPerformance();
            p.setStaffId(staff.getId());
            p.setStaffName(staff.getFullName());
            p.setResolvedCount(resolved.size());

            if (!resolved.isEmpty()) {
                long totalMinutes = 0;
                int count = 0;
                int ratingSum = 0;
                int ratingCount = 0;

                for (Complaint c : resolved) {
                    if (c.getResolvedAt() != null && c.getCreatedAt() != null) {
                        totalMinutes += Duration.between(c.getCreatedAt(), c.getResolvedAt()).toMinutes();
                        count++;
                    }
                    if (c.getRating() != null) {
                        ratingSum += c.getRating();
                        ratingCount++;
                    }
                }
                if (count > 0) {
                    p.setAverageResolutionTimeHours((double) (totalMinutes / count) / 60.0);
                }
                if (ratingCount > 0) {
                    p.setAverageRating((double) ratingSum / ratingCount);
                }
            }
            performances.add(p);
        }
        return performances;
    }

    private String formatDuration(long minutes) {
        if (minutes < 60) return minutes + " mins";
        long hours = minutes / 60;
        if (hours < 24) return hours + " hours";
        long days = hours / 24;
        return days + " days";
    }
}
