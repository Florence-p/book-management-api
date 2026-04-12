package com.bookmanagement.bookmanagementapp.service;

import com.bookmanagement.bookmanagementapp.dto.ReviewCreateRequest;
import com.bookmanagement.bookmanagementapp.dto.ReviewResponse;
import com.bookmanagement.bookmanagementapp.dto.ReviewUpdateRequest;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(Long bookId, ReviewCreateRequest request);

    List<ReviewResponse> getReviewsByBook(Long bookId);

    ReviewResponse updateReview(Long id, ReviewUpdateRequest request);

    void deleteReview(Long id);
}
