package com.campus.portal.config;

import com.campus.portal.entity.*;
import com.campus.portal.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final CategoryRepository categoryRepository;
    private final ComplaintRepository complaintRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
                           DepartmentRepository departmentRepository, CategoryRepository categoryRepository,
                           ComplaintRepository complaintRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.categoryRepository = categoryRepository;
        this.complaintRepository = complaintRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Seed Roles
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
        }

        // Cleanup Redundant Departments and Categories
        Map<String, String> mapping = Map.of(
            "Infrastructure", "Infrastructure & Maintenance",
            "IT Support", "Internet & IT Issues",
            "Hostel", "Hostel Issues",
            "Academic", "Academic & Classroom Issues",
            "Library", "Library Issues",
            "Security", "Safety & Security",
            "Administration", "Administrative Issues"
        );

        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            String oldDeptName = entry.getKey();
            String newDeptName = entry.getValue();

            departmentRepository.findByName(oldDeptName)
                .ifPresent(oldDept -> {
                    // Try to find the new department (it might not be created yet)
                    Department newDept = departmentRepository.findByName(newDeptName)
                        .orElseGet(() -> {
                            Department d = new Department();
                            d.setName(newDeptName);
                            d.setDescription("Modernized department name");
                            return departmentRepository.save(d);
                        });

                    // Update users to the new department
                    userRepository.findAll().stream()
                        .filter(u -> u.getDepartment() != null && u.getDepartment().getId().equals(oldDept.getId()))
                        .forEach(u -> {
                            u.setDepartment(newDept);
                            userRepository.save(u);
                        });

                    // Update complaints to the new department
                    complaintRepository.findAll().stream()
                        .filter(c -> c.getDepartment() != null && c.getDepartment().getId().equals(oldDept.getId()))
                        .forEach(c -> {
                            c.setDepartment(newDept);
                            complaintRepository.save(c);
                        });

                    // Delete categories linked to the old department
                    categoryRepository.findByDepartmentId(oldDept.getId()).forEach(cat -> {
                        // Check if categories are used in complaints
                        complaintRepository.findAll().stream()
                            .filter(comp -> comp.getCategory() != null && comp.getCategory().getId().equals(cat.getId()))
                            .forEach(comp -> {
                                comp.setCategory(null); // Or map to a new category if possible
                                complaintRepository.save(comp);
                            });
                        categoryRepository.delete(cat);
                    });

                    // Finally delete the old department
                    departmentRepository.delete(oldDept);
                    System.out.println("✅ Migrated and deleted redundant department: " + oldDeptName);
                });
        }

        // Seed Departments
        String[][] departments = {
            {"Infrastructure & Maintenance", "Campus infrastructure and building maintenance"},
            {"Internet & IT Issues", "WiFi, network, and computer related problems"},
            {"Hostel Issues", "Hostel accommodation and mess issues"},
            {"Academic & Classroom Issues", "Classroom equipment and academic facility issues"},
            {"Library Issues", "Library resources and facilities"},
            {"Safety & Security", "Campus safety and security concerns"},
            {"Administrative Issues", "Fees, ID cards, and portal related issues"},
            {"Others", "Other miscellaneous campus issues"}
        };

        for (String[] dept : departments) {
            if (!departmentRepository.existsByName(dept[0])) {
                Department d = new Department();
                d.setName(dept[0]);
                d.setDescription(dept[1]);
                departmentRepository.save(d);
            }
        }

        // Seed Categories
        String[][] categories = {
            // Infrastructure
            {"Broken Classroom Furniture", "Issues with desks, chairs, etc.", "Infrastructure & Maintenance"},
            {"Fan / Light Not Working", "Electrical fixture issues in classrooms", "Infrastructure & Maintenance"},
            {"Water Leakage", "Plumbing issues on campus", "Infrastructure & Maintenance"},
            {"Damaged Doors or Windows", "Structural maintenance issues", "Infrastructure & Maintenance"},
            {"Washroom Maintenance Issue", "Cleanliness or plumbing in washrooms", "Infrastructure & Maintenance"},
            {"Lift / Elevator Not Working", "Elevator maintenance issues", "Infrastructure & Maintenance"},
            {"Infrastructure - Others", "Other infrastructure issues", "Infrastructure & Maintenance"},

            // IT
            {"Campus WiFi Not Working", "WiFi connectivity issues", "Internet & IT Issues"},
            {"Slow Internet Speed", "Network performance issues", "Internet & IT Issues"},
            {"Network Connectivity Problem", "Wired or wireless network issues", "Internet & IT Issues"},
            {"WiFi Login Issue", "Authentication issues with campus WiFi", "Internet & IT Issues"},
            {"Computer System Not Working", "Lab or office computer hardware issues", "Internet & IT Issues"},
            {"Software Installation Issue", "Problems with academic software", "Internet & IT Issues"},
            {"IT - Others", "Other IT issues", "Internet & IT Issues"},

            // Hostel
            {"Room Maintenance Problem", "Issues within hostel rooms", "Hostel Issues"},
            {"Water Supply Issue", "Water availability in hostels", "Hostel Issues"},
            {"Electricity Problem in Hostel", "Power issues in hostel blocks", "Hostel Issues"},
            {"Hostel Cleanliness Issue", "General hostel hygiene", "Hostel Issues"},
            {"Mess Food Quality Issue", "Problems with mess food", "Hostel Issues"},
            {"Hostel - Others", "Other hostel issues", "Hostel Issues"},

            // Academic
            {"Projector Not Working", "AV equipment issues in classrooms", "Academic & Classroom Issues"},
            {"Smart Board Not Working", "Digital board issues", "Academic & Classroom Issues"},
            {"Lab Equipment Not Working", "Scientific or computer lab gear issues", "Academic & Classroom Issues"},
            {"Classroom Cleanliness Issue", "Hygiene in academic areas", "Academic & Classroom Issues"},
            {"Classroom Seating Problem", "Arrangement or capacity issues", "Academic & Classroom Issues"},
            {"Academic - Others", "Other academic facility issues", "Academic & Classroom Issues"},

            // Library
            {"Book Not Available / Missing", "Issues with library collection", "Library Issues"},
            {"Library Computer Not Working", "Public computer issues in library", "Library Issues"},
            {"WiFi Issue in Library", "Connectivity in library area", "Library Issues"},
            {"Library Seating Problem", "Space or furniture in library", "Library Issues"},
            {"Library - Others", "Other library issues", "Library Issues"},

            // Security
            {"Security Concern", "General safety reports", "Safety & Security"},
            {"Suspicious Activity", "Reporting unusual behavior", "Safety & Security"},
            {"Poor Lighting in Campus Area", "Safety issues due to visibility", "Safety & Security"},
            {"Unauthorized Access", "Security breaches or trespassing", "Safety & Security"},
            {"Security - Others", "Other security issues", "Safety & Security"},

            // Admin
            {"ID Card Issue", "Problems with student/staff ID cards", "Administrative Issues"},
            {"Fee Payment Issue", "Issues during fee transactions", "Administrative Issues"},
            {"Certificate Request Delay", "Delays in official documentation", "Administrative Issues"},
            {"Student Portal Login Issue", "Authentication problems with portal", "Administrative Issues"},
            {"Exam Related Issue", "Logistical problems with exams", "Administrative Issues"},
            {"Admin - Others", "Other administrative issues", "Administrative Issues"},

            // Other
            {"Others", "Miscellaneous category", "Others"}
        };

        for (String[] cat : categories) {
            if (!categoryRepository.existsByName(cat[0])) {
                departmentRepository.findAll().stream()
                    .filter(d -> d.getName().equals(cat[2]))
                    .findFirst()
                    .ifPresent(dept -> {
                        Category c = new Category();
                        c.setName(cat[0]);
                        c.setDescription(cat[1]);
                        c.setDepartment(dept);
                        categoryRepository.save(c);
                    });
            }
        }

        // Seed Admin User
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@campus.edu");
            admin.setFullName("System Administrator");
            admin.setActive(true);
            roleRepository.findByName(RoleName.ROLE_ADMIN).ifPresent(admin::setRole);
            userRepository.save(admin);
            System.out.println("✅ Admin user created: username=admin, password=admin123");
        }

        // Seed demo staff for IT
        if (!userRepository.existsByUsername("staff_it")) {
            departmentRepository.findAll().stream()
                .filter(d -> d.getName().equals("Internet & IT Issues"))
                .findFirst()
                .ifPresent(dept -> {
                    User staff = new User();
                    staff.setUsername("staff_it");
                    staff.setPassword(passwordEncoder.encode("staff123"));
                    staff.setEmail("staff_it@campus.edu");
                    staff.setFullName("IT Staff Member");
                    staff.setDepartment(dept);
                    staff.setActive(true);
                    roleRepository.findByName(RoleName.ROLE_STAFF).ifPresent(staff::setRole);
                    userRepository.save(staff);
                });
            System.out.println("✅ Staff user created: username=staff_it, password=staff123");
        }

        // Seed demo student
        if (!userRepository.existsByUsername("student1")) {
            User student = new User();
            student.setUsername("student1");
            student.setPassword(passwordEncoder.encode("student123"));
            student.setEmail("student1@campus.edu");
            student.setFullName("Demo Student");
            student.setActive(true);
            roleRepository.findByName(RoleName.ROLE_STUDENT).ifPresent(student::setRole);
            userRepository.save(student);
            System.out.println("✅ Student user created: username=student1, password=student123");
        }

        if (complaintRepository.countByStatus(ComplaintStatus.RESOLVED) < 5) {
            seedHistoricalComplaints();
        }

        System.out.println("✅ Data initialization complete.");
    }

    private void seedHistoricalComplaints() {
        User student = userRepository.findByUsername("student1").orElse(null);
        User staff = userRepository.findByUsername("staff_it").orElse(null);
        Category wifiCat = categoryRepository.findAll().stream()
            .filter(c -> c.getName().contains("WiFi"))
            .findFirst().orElse(null);
        Department itDept = departmentRepository.findByName("Internet & IT Issues").orElse(null);

        if (student != null && staff != null && wifiCat != null && itDept != null) {
            // Complaint 1: Resolved in 2 hours
            createResolvedComplaint("Slow WiFi in Block A", student, staff, wifiCat, itDept, 120, 5);
            // Complaint 2: Resolved in 5 hours
            createResolvedComplaint("Cannot login to WiFi", student, staff, wifiCat, itDept, 300, 4);
            // Complaint 3: Resolved in 1 day (1440 mins)
            createResolvedComplaint("No network in Lab", student, staff, wifiCat, itDept, 1440, 3);
            
            System.out.println("✅ Seeded historical resolved complaints for AI data.");
        }
    }

    private void createResolvedComplaint(String title, User student, User staff, Category cat, Department dept, int minutesTaken, int rating) {
        Complaint c = new Complaint();
        c.setTitle(title);
        c.setDescription("Auto-seeded historical complaint for analytics.");
        c.setStudent(student);
        c.setAssignedStaff(staff);
        c.setCategory(cat);
        c.setDepartment(dept);
        c.setStatus(ComplaintStatus.RESOLVED);
        c.setRating(rating);
        
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        c.setCreatedAt(now.minusMinutes(minutesTaken + 10)); // Created slightly before
        c.setResolvedAt(now.minusMinutes(10)); // Resolved 10 mins ago
        
        complaintRepository.save(c);
    }
}
