package com.bookmanagement.bookmanagementapp.repository;

import com.bookmanagement.bookmanagementapp.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByBookIdOrderByCreatedAtDesc(Long bookId);

    boolean existsByBookIdAndUserId(Long bookId, Long userId);

    @Query("select avg(r.rating) from Review r where r.book.id = :bookId")
    Double findAverageRatingByBookId(@Param("bookId") Long bookId);
}
