package com.bookmanagement.bookmanagementapp.service.impl;

import com.bookmanagement.bookmanagementapp.dto.ReviewCreateRequest;
import com.bookmanagement.bookmanagementapp.dto.ReviewResponse;
import com.bookmanagement.bookmanagementapp.dto.ReviewUpdateRequest;
import com.bookmanagement.bookmanagementapp.entity.Book;
import com.bookmanagement.bookmanagementapp.entity.Review;
import com.bookmanagement.bookmanagementapp.entity.User;
import com.bookmanagement.bookmanagementapp.exception.DuplicateResourceException;
import com.bookmanagement.bookmanagementapp.exception.ResourceNotFoundException;
import com.bookmanagement.bookmanagementapp.repository.BookRepository;
import com.bookmanagement.bookmanagementapp.repository.ReviewRepository;
import com.bookmanagement.bookmanagementapp.repository.UserRepository;
import com.bookmanagement.bookmanagementapp.service.ReviewService;
import com.bookmanagement.bookmanagementapp.util.mapper.ApiMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ApiMapper apiMapper;

    @Override
    public ReviewResponse createReview(Long bookId, ReviewCreateRequest request) {
        Book book = getBook(bookId);
        User user = getUser(request.userId());
        if (reviewRepository.existsByBookIdAndUserId(bookId, request.userId())) {
            throw new DuplicateResourceException("User has already reviewed this book");
        }

        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setRating(request.rating());
        review.setComment(request.comment().trim());

        Review savedReview = reviewRepository.save(review);
        recalculateBookRating(bookId);
        return apiMapper.toReviewResponse(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByBook(Long bookId) {
        getBook(bookId);
        return reviewRepository.findAllByBookIdOrderByCreatedAtDesc(bookId).stream()
                .map(apiMapper::toReviewResponse)
                .toList();
    }

    @Override
    public ReviewResponse updateReview(Long id, ReviewUpdateRequest request) {
        Review review = getReview(id);
        review.setRating(request.rating());
        review.setComment(request.comment().trim());
        Review updatedReview = reviewRepository.save(review);
        recalculateBookRating(review.getBook().getId());
        return apiMapper.toReviewResponse(updatedReview);
    }

    @Override
    public void deleteReview(Long id) {
        Review review = getReview(id);
        Long bookId = review.getBook().getId();
        reviewRepository.delete(review);
        recalculateBookRating(bookId);
    }

    private void recalculateBookRating(Long bookId) {
        Book book = getBook(bookId);
        Double average = reviewRepository.findAverageRatingByBookId(bookId);
        book.setRating(average == null
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP));
        bookRepository.save(book);
    }

    private Book getBook(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Review getReview(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
    }
}
