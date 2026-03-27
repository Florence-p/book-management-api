package com.bookmanagement.bookmanagementapp.repository;

import com.bookmanagement.bookmanagementapp.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author,Long> {
}
