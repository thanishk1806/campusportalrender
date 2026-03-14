package com.campus.portal.controller;

import com.campus.portal.dto.ComplaintDTO;
import com.campus.portal.dto.CommentDTO;
import com.campus.portal.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping("/student/complaints")
    public ResponseEntity<ComplaintDTO.Response> createComplaint(
            @Valid @RequestBody ComplaintDTO.CreateRequest request,
            Authentication auth) {
        return ResponseEntity.ok(complaintService.createComplaint(request, auth.getName()));
    }

    @GetMapping("/student/complaints")
    public ResponseEntity<List<ComplaintDTO.Response>> getMyComplaints(Authentication auth) {
        return ResponseEntity.ok(complaintService.getComplaintsByStudent(auth.getName()));
    }

    @GetMapping("/staff/complaints")
    public ResponseEntity<List<ComplaintDTO.Response>> getAssignedComplaints(Authentication auth) {
        return ResponseEntity.ok(complaintService.getComplaintsByStaff(auth.getName()));
    }

    @PutMapping("/staff/complaints/{id}/status")
    public ResponseEntity<ComplaintDTO.Response> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintDTO.UpdateStatusRequest request,
            Authentication auth) {
        return ResponseEntity.ok(complaintService.updateStatus(id, request, auth.getName()));
    }

    @GetMapping("/admin/complaints")
    public ResponseEntity<List<ComplaintDTO.Response>> getAllComplaints() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    @PutMapping("/admin/complaints/{id}/assign")
    public ResponseEntity<ComplaintDTO.Response> assignComplaint(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintDTO.AssignRequest request) {
        return ResponseEntity.ok(complaintService.assignComplaint(id, request));
    }

    @PutMapping("/admin/complaints/{id}/status")
    public ResponseEntity<ComplaintDTO.Response> adminUpdateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintDTO.UpdateStatusRequest request,
            Authentication auth) {
        return ResponseEntity.ok(complaintService.updateStatus(id, request, auth.getName()));
    }

    @GetMapping("/complaints/{id}")
    public ResponseEntity<ComplaintDTO.Response> getComplaint(@PathVariable Long id) {
        return ResponseEntity.ok(complaintService.getComplaintById(id));
    }

    @PostMapping("/student/complaints/{id}/rate")
    public ResponseEntity<ComplaintDTO.Response> rateComplaint(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintDTO.RateRequest request,
            Authentication auth) {
        return ResponseEntity.ok(complaintService.rateComplaint(id, request, auth.getName()));
    }

    @PostMapping("/complaints/comment")
    public ResponseEntity<CommentDTO.Response> addComment(
            @Valid @RequestBody CommentDTO.CreateRequest request,
            Authentication auth) {
        return ResponseEntity.ok(complaintService.addComment(request, auth.getName()));
    }
}
