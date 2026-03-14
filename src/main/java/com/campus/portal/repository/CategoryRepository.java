package com.campus.portal.repository;

import com.campus.portal.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByDepartmentId(Long departmentId);
    boolean existsByName(String name);
}
