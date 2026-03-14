package com.campus.portal.repository;

import com.campus.portal.entity.Complaint;
import com.campus.portal.entity.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    List<Complaint> findByAssignedStaffIdOrderByCreatedAtDesc(Long staffId);
    List<Complaint> findByDepartmentIdOrderByCreatedAtDesc(Long departmentId);
    List<Complaint> findAllByOrderByCreatedAtDesc();
    List<Complaint> findByCategoryIdAndStatus(Long categoryId, ComplaintStatus status);
    List<Complaint> findByStatus(ComplaintStatus status);
    long countByStatus(ComplaintStatus status);

    @Query("SELECT c.status, COUNT(c) FROM Complaint c GROUP BY c.status")
    List<Object[]> countByStatusGrouped();

    @Query("SELECT c.department.name, COUNT(c) FROM Complaint c GROUP BY c.department")
    List<Object[]> countByDepartment();
}
