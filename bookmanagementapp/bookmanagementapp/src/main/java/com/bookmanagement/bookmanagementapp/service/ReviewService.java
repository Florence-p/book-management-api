package com.bookmanagement.bookmanagementapp.service;

import com.bookmanagement.bookmanagementapp.dto.ReviewRequest;
import com.bookmanagement.bookmanagementapp.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(Long bookId, ReviewRequest request);
    List<ReviewResponse> getReviewsByBook(Long bookId);
    ReviewResponse updateReview(Long id, ReviewRequest request);
    void deleteReview(Long id);
}