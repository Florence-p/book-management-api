package com.bookmanagement.bookmanagementapp.repository;

import com.bookmanagement.bookmanagementapp.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
