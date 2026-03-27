package com.bookmanagement.bookmanagementapp.service;

import com.bookmanagement.bookmanagementapp.dto.ReviewRequest;
import com.bookmanagement.bookmanagementapp.dto.ReviewResponse;
import com.bookmanagement.bookmanagementapp.entity.*;
import com.bookmanagement.bookmanagementapp.exception.ResourceNotFoundException;
import com.bookmanagement.bookmanagementapp.repository.BookRepository;
import com.bookmanagement.bookmanagementapp.repository.ReviewRepository;
import com.bookmanagement.bookmanagementapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Book book;
    private User user;
    private Review review;
    private ReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        Author author = new Author();
        author.setId(1L);
        author.setName("Chimamanda Ngozi Adichie");
        author.setEmail("chimamanda@authors.com");

        book = new Book();
        book.setId(1L);
        book.setTitle("Half of a Yellow Sun");
        book.setIsbn("9780007200283");
        book.setAuthor(author);
        book.setCategories(new ArrayList<>());
        book.setReviews(new ArrayList<>());
        book.setRating(0.0);

        user = new User();
        user.setId(1L);
        user.setUsername("emeka_reader");
        user.setEmail("emeka@readers.com");
        user.setRole(Role.USER);

        review = new Review();
        review.setId(1L);
        review.setRating(5);
        review.setComment("Absolutely breathtaking!");
        review.setBook(book);
        review.setUser(user);
        review.setCreatedAt(LocalDateTime.now());

        reviewRequest = new ReviewRequest(5, "Absolutely breathtaking!", 1L);
    }

    @Test
    void shouldCreateReviewSuccessfully() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewRepository.findByBook(book)).thenReturn(List.of(review));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        ReviewResponse response = reviewService.createReview(1L, reviewRequest);

        assertThat(response).isNotNull();
        assertThat(response.getRating()).isEqualTo(5);
        assertThat(response.getComment()).isEqualTo("Absolutely breathtaking!");
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void shouldGetReviewsByBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(reviewRepository.findByBook(book)).thenReturn(List.of(review));

        List<ReviewResponse> responses = reviewService.getReviewsByBook(1L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getRating()).isEqualTo(5);
    }

    @Test
    void shouldThrowExceptionWhenBookNotFoundOnReview() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(99L, reviewRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book not found with id: 99");
    }

    @Test
    void shouldUpdateReviewSuccessfully() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewRepository.findByBook(book)).thenReturn(List.of(review));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        ReviewResponse response = reviewService.updateReview(1L, reviewRequest);

        assertThat(response).isNotNull();
        assertThat(response.getRating()).isEqualTo(5);
    }

    @Test
    void shouldDeleteReviewSuccessfully() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        doNothing().when(reviewRepository).delete(review);
        when(reviewRepository.findByBook(book)).thenReturn(new ArrayList<>());
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        reviewService.deleteReview(1L);

        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    void shouldThrowExceptionWhenReviewNotFound() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.deleteReview(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review not found with id: 99");
    }
}