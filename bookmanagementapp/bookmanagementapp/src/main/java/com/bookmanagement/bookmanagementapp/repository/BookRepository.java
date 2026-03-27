package com.bookmanagement.bookmanagementapp.repository;

import com.bookmanagement.bookmanagementapp.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
