package com.campus.portal.dto;

import com.campus.portal.entity.ComplaintStatus;
import com.campus.portal.entity.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class ComplaintDTO {

    public static class CreateRequest {
        @NotBlank
        private String title;
        @NotBlank
        private String description;
        @NotNull
        private Long categoryId;
        @NotNull
        private Long departmentId;
        private Priority priority = Priority.MEDIUM;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

        public Priority getPriority() { return priority; }
        public void setPriority(Priority priority) { this.priority = priority; }
    }

    public static class UpdateStatusRequest {
        @NotNull
        private ComplaintStatus status;

        public ComplaintStatus getStatus() { return status; }
        public void setStatus(ComplaintStatus status) { this.status = status; }
    }

    public static class RateRequest {
        @NotNull
        private Integer rating;

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
    }

    public static class AssignRequest {
        @NotNull
        private Long staffId;

        public Long getStaffId() { return staffId; }
        public void setStaffId(Long staffId) { this.staffId = staffId; }
    }

    public static class Response {
        private Long id;
        private String title;
        private String description;
        private ComplaintStatus status;
        private Priority priority;
        private String studentName;
        private Long studentId;
        private String assignedStaffName;
        private Long assignedStaffId;
        private String categoryName;
        private String departmentName;
        private Long departmentId;
        private Integer rating;
        private List<CommentDTO.Response> comments;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime resolvedAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public ComplaintStatus getStatus() { return status; }
        public void setStatus(ComplaintStatus status) { this.status = status; }

        public Priority getPriority() { return priority; }
        public void setPriority(Priority priority) { this.priority = priority; }

        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }

        public String getAssignedStaffName() { return assignedStaffName; }
        public void setAssignedStaffName(String assignedStaffName) { this.assignedStaffName = assignedStaffName; }

        public Long getAssignedStaffId() { return assignedStaffId; }
        public void setAssignedStaffId(Long assignedStaffId) { this.assignedStaffId = assignedStaffId; }

        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

        public String getDepartmentName() { return departmentName; }
        public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }

        public List<CommentDTO.Response> getComments() { return comments; }
        public void setComments(List<CommentDTO.Response> comments) { this.comments = comments; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

        public LocalDateTime getResolvedAt() { return resolvedAt; }
        public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    }
}
