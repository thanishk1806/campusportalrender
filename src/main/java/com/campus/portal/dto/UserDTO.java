package com.campus.portal.dto;

import java.util.Map;

public class UserDTO {

    public static class Response {
        private Long id;
        private String username;
        private String email;
        private String fullName;
        private String phone;
        private String role;
        private Long departmentId;
        private String departmentName;
        private Boolean active;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

        public String getDepartmentName() { return departmentName; }
        public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

        public Boolean getActive() { return active; }
        public void setActive(Boolean active) { this.active = active; }
    }

    public static class DashboardStats {
        private long totalComplaints;
        private long pendingComplaints;
        private long inProgressComplaints;
        private long resolvedComplaints;
        private Map<String, Long> complaintsByDepartment;
        private long totalUsers;
        private long totalStudents;
        private long totalStaff;

        public long getTotalComplaints() { return totalComplaints; }
        public void setTotalComplaints(long totalComplaints) { this.totalComplaints = totalComplaints; }

        public long getPendingComplaints() { return pendingComplaints; }
        public void setPendingComplaints(long pendingComplaints) { this.pendingComplaints = pendingComplaints; }

        public long getInProgressComplaints() { return inProgressComplaints; }
        public void setInProgressComplaints(long inProgressComplaints) { this.inProgressComplaints = inProgressComplaints; }

        public long getResolvedComplaints() { return resolvedComplaints; }
        public void setResolvedComplaints(long resolvedComplaints) { this.resolvedComplaints = resolvedComplaints; }

        public Map<String, Long> getComplaintsByDepartment() { return complaintsByDepartment; }
        public void setComplaintsByDepartment(Map<String, Long> complaintsByDepartment) { this.complaintsByDepartment = complaintsByDepartment; }

        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

        public long getTotalStudents() { return totalStudents; }
        public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }

        public long getTotalStaff() { return totalStaff; }
        public void setTotalStaff(long totalStaff) { this.totalStaff = totalStaff; }
    }
}
