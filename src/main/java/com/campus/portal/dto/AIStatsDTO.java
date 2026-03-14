package com.campus.portal.dto;

import java.util.List;
import java.util.Map;

public class AIStatsDTO {

    public static class EstimationResponse {
        private String categoryName;
        private String estimatedTime; // e.g., "2 hours", "1 day"
        private long averageMinutes;

        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

        public String getEstimatedTime() { return estimatedTime; }
        public void setEstimatedTime(String estimatedTime) { this.estimatedTime = estimatedTime; }

        public long getAverageMinutes() { return averageMinutes; }
        public void setAverageMinutes(long averageMinutes) { this.averageMinutes = averageMinutes; }
    }

    public static class DepartmentPerformance {
        private String departmentName;
        private long totalResolved;
        private double averageResolutionTimeHours;

        public String getDepartmentName() { return departmentName; }
        public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

        public long getTotalResolved() { return totalResolved; }
        public void setTotalResolved(long totalResolved) { this.totalResolved = totalResolved; }

        public double getAverageResolutionTimeHours() { return averageResolutionTimeHours; }
        public void setAverageResolutionTimeHours(double averageResolutionTimeHours) { this.averageResolutionTimeHours = averageResolutionTimeHours; }
    }

    public static class StaffPerformance {
        private Long staffId;
        private String staffName;
        private long resolvedCount;
        private double averageResolutionTimeHours;
        private double averageRating;

        public Long getStaffId() { return staffId; }
        public void setStaffId(Long staffId) { this.staffId = staffId; }

        public String getStaffName() { return staffName; }
        public void setStaffName(String staffName) { this.staffName = staffName; }

        public long getResolvedCount() { return resolvedCount; }
        public void setResolvedCount(long resolvedCount) { this.resolvedCount = resolvedCount; }

        public double getAverageResolutionTimeHours() { return averageResolutionTimeHours; }
        public void setAverageResolutionTimeHours(double averageResolutionTimeHours) { this.averageResolutionTimeHours = averageResolutionTimeHours; }

        public double getAverageRating() { return averageRating; }
        public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    }
}
