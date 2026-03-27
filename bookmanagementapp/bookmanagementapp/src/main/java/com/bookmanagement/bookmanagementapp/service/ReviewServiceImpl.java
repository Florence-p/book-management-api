package com.bookmanagement.bookmanagementapp.service;

import com.bookmanagement.bookmanagementapp.dto.*;
import com.bookmanagement.bookmanagementapp.entity.Book;
import com.bookmanagement.bookmanagementapp.entity.Review;
import com.bookmanagement.bookmanagementapp.entity.User;
import com.bookmanagement.bookmanagementapp.exception.ResourceNotFoundException;
import com.bookmanagement.bookmanagementapp.repository.BookRepository;
import com.bookmanagement.bookmanagementapp.repository.ReviewRepository;
import com.bookmanagement.bookmanagementapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    public ReviewResponse createReview(Long bookId, ReviewRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Review review = new Review();
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setBook(book);
        review.setUser(user);
        review.setCreatedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);
        updateBookRating(book);
        return mapToResponse(saved);
    }

    @Override
    public List<ReviewResponse> getReviewsByBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        return reviewRepository.findByBook(book)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse updateReview(Long id, ReviewRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        Review updated = reviewRepository.save(review);
        updateBookRating(review.getBook());
        return mapToResponse(updated);
    }

    @Override
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        Book book = review.getBook();
        reviewRepository.delete(review);
        updateBookRating(book);
    }

    private void updateBookRating(Book book) {
        List<Review> reviews = reviewRepository.findByBook(book);
        double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        book.setRating(average);
        bookRepository.save(book);
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .book(BookSummaryDto.builder()
                        .id(review.getBook().getId())
                        .title(review.getBook().getTitle())
                        .build())
                .user(UserSummaryDto.builder()
                        .id(review.getUser().getId())
                        .username(review.getUser().getUsername())
                        .build())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}