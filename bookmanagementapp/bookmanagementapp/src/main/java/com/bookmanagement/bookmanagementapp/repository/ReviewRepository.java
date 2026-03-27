package com.bookmanagement.bookmanagementapp.repository;

import com.bookmanagement.bookmanagementapp.entity.Book;
import com.bookmanagement.bookmanagementapp.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository <Review, Long> {
    List<Review> findByBook(Book book);
}
