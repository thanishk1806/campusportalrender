package com.campus.portal.service;

import com.campus.portal.dto.ComplaintDTO;
import com.campus.portal.dto.CommentDTO;
import com.campus.portal.entity.*;
import com.campus.portal.exception.ResourceNotFoundException;
import com.campus.portal.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final DepartmentRepository departmentRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;

    public ComplaintService(ComplaintRepository complaintRepository, UserRepository userRepository,
                            CategoryRepository categoryRepository, DepartmentRepository departmentRepository,
                            CommentRepository commentRepository, NotificationService notificationService) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.departmentRepository = departmentRepository;
        this.commentRepository = commentRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public ComplaintDTO.Response createComplaint(ComplaintDTO.CreateRequest request, String username) {
        User student = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Department department = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Complaint complaint = new Complaint();
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setCategory(category);
        complaint.setDepartment(department);
        complaint.setPriority(request.getPriority());
        complaint.setStudent(student);
        complaint.setStatus(ComplaintStatus.PENDING);

        List<User> staffList = userRepository.findByDepartmentIdAndRoleName(
            department.getId(), RoleName.ROLE_STAFF
        );
        if (!staffList.isEmpty()) {
            User assignedStaff = staffList.get(0);
            complaint.setAssignedStaff(assignedStaff);
            notificationService.createNotification(assignedStaff,
                "New complaint assigned to you: " + complaint.getTitle());
        }

        Complaint saved = complaintRepository.save(complaint);
        notificationService.createNotification(student,
            "Your complaint has been submitted: " + saved.getTitle());

        return toResponse(saved);
    }

    public List<ComplaintDTO.Response> getComplaintsByStudent(String username) {
        User student = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return complaintRepository.findByStudentIdOrderByCreatedAtDesc(student.getId())
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ComplaintDTO.Response> getComplaintsByStaff(String username) {
        User staff = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return complaintRepository.findByAssignedStaffIdOrderByCreatedAtDesc(staff.getId())
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ComplaintDTO.Response> getAllComplaints() {
        return complaintRepository.findAllByOrderByCreatedAtDesc()
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ComplaintDTO.Response getComplaintById(Long id) {
        return toResponse(complaintRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Complaint not found")));
    }

    @Transactional
    public ComplaintDTO.Response updateStatus(Long id, ComplaintDTO.UpdateStatusRequest request, String username) {
        Complaint complaint = complaintRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Enforce role-based status boundaries
        if (user.getRole().getName() == RoleName.ROLE_ADMIN) {
            if (request.getStatus() != ComplaintStatus.IN_PROGRESS) {
                throw new IllegalArgumentException("Admin can only set status to IN_PROGRESS");
            }
        } else if (user.getRole().getName() == RoleName.ROLE_STAFF) {
            if (request.getStatus() != ComplaintStatus.RESOLVED) {
                throw new IllegalArgumentException("Staff can only set status to RESOLVED");
            }
        }

        if (request.getStatus() == ComplaintStatus.RESOLVED) {
            complaint.setResolvedAt(java.time.LocalDateTime.now());
        }
        complaint.setStatus(request.getStatus());
        Complaint saved = complaintRepository.save(complaint);
        notificationService.createNotification(complaint.getStudent(),
            "Your complaint '" + complaint.getTitle() + "' status updated to: " + request.getStatus());
        return toResponse(saved);
    }

    @Transactional
    public ComplaintDTO.Response rateComplaint(Long id, ComplaintDTO.RateRequest request, String username) {
        Complaint complaint = complaintRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));
        User student = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!complaint.getStudent().getId().equals(student.getId())) {
            throw new IllegalArgumentException("Only the student who submitted the complaint can rate it");
        }

        if (complaint.getStatus() != ComplaintStatus.RESOLVED) {
            throw new IllegalArgumentException("Complaint must be RESOLVED before it can be rated");
        }

        complaint.setRating(request.getRating());
        Complaint saved = complaintRepository.save(complaint);
        
        if (complaint.getAssignedStaff() != null) {
            notificationService.createNotification(complaint.getAssignedStaff(),
                "Your resolved complaint has been rated: " + request.getRating() + " stars");
        }

        return toResponse(saved);
    }

    @Transactional
    public ComplaintDTO.Response assignComplaint(Long id, ComplaintDTO.AssignRequest request) {
        Complaint complaint = complaintRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));
        User staff = userRepository.findById(request.getStaffId())
            .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
        complaint.setAssignedStaff(staff);
        Complaint saved = complaintRepository.save(complaint);
        notificationService.createNotification(staff,
            "Complaint assigned to you: " + complaint.getTitle());
        return toResponse(saved);
    }

    @Transactional
    public CommentDTO.Response addComment(CommentDTO.CreateRequest request, String username) {
        User author = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Complaint complaint = complaintRepository.findById(request.getComplaintId())
            .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setAuthor(author);
        comment.setComplaint(complaint);
        Comment saved = commentRepository.save(comment);

        if (!author.getId().equals(complaint.getStudent().getId())) {
            notificationService.createNotification(complaint.getStudent(),
                "New comment on your complaint: " + complaint.getTitle());
        }

        return toCommentResponse(saved);
    }

    private ComplaintDTO.Response toResponse(Complaint c) {
        ComplaintDTO.Response r = new ComplaintDTO.Response();
        r.setId(c.getId());
        r.setTitle(c.getTitle());
        r.setDescription(c.getDescription());
        r.setStatus(c.getStatus());
        r.setPriority(c.getPriority());
        r.setStudentName(c.getStudent().getFullName());
        r.setStudentId(c.getStudent().getId());
        r.setCreatedAt(c.getCreatedAt());
        r.setUpdatedAt(c.getUpdatedAt());
        r.setResolvedAt(c.getResolvedAt());
        if (c.getAssignedStaff() != null) {
            r.setAssignedStaffName(c.getAssignedStaff().getFullName());
            r.setAssignedStaffId(c.getAssignedStaff().getId());
        }
        if (c.getCategory() != null) r.setCategoryName(c.getCategory().getName());
        if (c.getDepartment() != null) {
            r.setDepartmentName(c.getDepartment().getName());
            r.setDepartmentId(c.getDepartment().getId());
        }
        r.setRating(c.getRating());
        List<Comment> comments = commentRepository.findByComplaintIdOrderByCreatedAtAsc(c.getId());
        r.setComments(comments.stream().map(this::toCommentResponse).collect(Collectors.toList()));
        return r;
    }

    private CommentDTO.Response toCommentResponse(Comment c) {
        CommentDTO.Response r = new CommentDTO.Response();
        r.setId(c.getId());
        r.setContent(c.getContent());
        r.setAuthorName(c.getAuthor().getFullName());
        r.setAuthorRole(c.getAuthor().getRole().getName().name());
        r.setCreatedAt(c.getCreatedAt());
        return r;
    }
}
