package com.campus.portal.service;

import com.campus.portal.dto.*;
import com.campus.portal.entity.*;
import com.campus.portal.exception.BadRequestException;
import com.campus.portal.exception.ResourceNotFoundException;
import com.campus.portal.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final CategoryRepository categoryRepository;
    private final ComplaintRepository complaintRepository;
    private final RoleRepository roleRepository;

    public AdminService(UserRepository userRepository, DepartmentRepository departmentRepository,
                        CategoryRepository categoryRepository, ComplaintRepository complaintRepository,
                        RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.categoryRepository = categoryRepository;
        this.complaintRepository = complaintRepository;
        this.roleRepository = roleRepository;
    }

    public UserDTO.DashboardStats getDashboardStats() {
        UserDTO.DashboardStats stats = new UserDTO.DashboardStats();
        stats.setTotalComplaints(complaintRepository.count());
        stats.setPendingComplaints(complaintRepository.countByStatus(ComplaintStatus.PENDING));
        stats.setInProgressComplaints(complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS));
        stats.setResolvedComplaints(complaintRepository.countByStatus(ComplaintStatus.RESOLVED));
        stats.setTotalUsers(userRepository.count());
        stats.setTotalStudents(userRepository.findByRoleName(RoleName.ROLE_STUDENT).size());
        stats.setTotalStaff(userRepository.findByRoleName(RoleName.ROLE_STAFF).size());

        Map<String, Long> byDept = new LinkedHashMap<>();
        complaintRepository.countByDepartment()
            .forEach(row -> byDept.put((String) row[0], (Long) row[1]));
        stats.setComplaintsByDepartment(byDept);
        return stats;
    }

    public List<UserDTO.Response> getAllUsers() {
        return userRepository.findAll().stream().map(this::toUserResponse).collect(Collectors.toList());
    }

    public List<UserDTO.Response> getStaffByDepartment(Long departmentId) {
        return userRepository.findByDepartmentIdAndRoleName(departmentId, RoleName.ROLE_STAFF)
            .stream().map(this::toUserResponse).collect(Collectors.toList());
    }

    public List<DepartmentDTO.Response> getAllDepartments() {
        return departmentRepository.findAll().stream().map(d -> {
            DepartmentDTO.Response r = new DepartmentDTO.Response();
            r.setId(d.getId());
            r.setName(d.getName());
            r.setDescription(d.getDescription());
            return r;
        }).collect(Collectors.toList());
    }

    public DepartmentDTO.Response createDepartment(DepartmentDTO.Request request) {
        if (departmentRepository.existsByName(request.getName()))
            throw new BadRequestException("Department already exists");
        Department d = new Department();
        d.setName(request.getName());
        d.setDescription(request.getDescription());
        Department saved = departmentRepository.save(d);
        DepartmentDTO.Response r = new DepartmentDTO.Response();
        r.setId(saved.getId());
        r.setName(saved.getName());
        r.setDescription(saved.getDescription());
        return r;
    }

    public List<CategoryDTO.Response> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::toCategoryResponse).collect(Collectors.toList());
    }

    public CategoryDTO.Response createCategory(CategoryDTO.Request request) {
        if (categoryRepository.existsByName(request.getName()))
            throw new BadRequestException("Category already exists");
        Department dept = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        Category c = new Category();
        c.setName(request.getName());
        c.setDescription(request.getDescription());
        c.setDepartment(dept);
        return toCategoryResponse(categoryRepository.save(c));
    }

    private UserDTO.Response toUserResponse(User u) {
        UserDTO.Response r = new UserDTO.Response();
        r.setId(u.getId());
        r.setUsername(u.getUsername());
        r.setEmail(u.getEmail());
        r.setFullName(u.getFullName());
        r.setPhone(u.getPhone());
        r.setActive(u.getActive());
        r.setRole(u.getRole().getName().name());
        if (u.getDepartment() != null) {
            r.setDepartmentId(u.getDepartment().getId());
            r.setDepartmentName(u.getDepartment().getName());
        }
        return r;
    }

    private CategoryDTO.Response toCategoryResponse(Category c) {
        CategoryDTO.Response r = new CategoryDTO.Response();
        r.setId(c.getId());
        r.setName(c.getName());
        r.setDescription(c.getDescription());
        if (c.getDepartment() != null) {
            r.setDepartmentId(c.getDepartment().getId());
            r.setDepartmentName(c.getDepartment().getName());
        }
        return r;
    }
}
