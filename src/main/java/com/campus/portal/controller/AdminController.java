package com.campus.portal.controller;

import com.campus.portal.dto.*;
import com.campus.portal.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<UserDTO.DashboardStats> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO.Response>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/staff/department/{departmentId}")
    public ResponseEntity<List<UserDTO.Response>> getStaffByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(adminService.getStaffByDepartment(departmentId));
    }

    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentDTO.Response>> getDepartments() {
        return ResponseEntity.ok(adminService.getAllDepartments());
    }

    @PostMapping("/departments")
    public ResponseEntity<DepartmentDTO.Response> createDepartment(@Valid @RequestBody DepartmentDTO.Request request) {
        return ResponseEntity.ok(adminService.createDepartment(request));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO.Response>> getCategories() {
        return ResponseEntity.ok(adminService.getAllCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryDTO.Response> createCategory(@Valid @RequestBody CategoryDTO.Request request) {
        return ResponseEntity.ok(adminService.createCategory(request));
    }
}
