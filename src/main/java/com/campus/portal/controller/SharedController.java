package com.campus.portal.controller;

import com.campus.portal.dto.CategoryDTO;
import com.campus.portal.dto.DepartmentDTO;
import com.campus.portal.entity.Notification;
import com.campus.portal.service.AdminService;
import com.campus.portal.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SharedController {

    private final AdminService adminService;
    private final NotificationService notificationService;

    public SharedController(AdminService adminService, NotificationService notificationService) {
        this.adminService = adminService;
        this.notificationService = notificationService;
    }

    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentDTO.Response>> getDepartments() {
        return ResponseEntity.ok(adminService.getAllDepartments());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO.Response>> getCategories() {
        return ResponseEntity.ok(adminService.getAllCategories());
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getNotifications(Authentication auth) {
        return ResponseEntity.ok(notificationService.getUserNotifications(auth.getName()));
    }

    @GetMapping("/notifications/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication auth) {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(auth.getName())));
    }

    @PostMapping("/notifications/mark-read")
    public ResponseEntity<String> markRead(Authentication auth) {
        notificationService.markAllRead(auth.getName());
        return ResponseEntity.ok("Marked all as read");
    }
}
