package com.bookmanagement.bookmanagementapp.service;

import com.bookmanagement.bookmanagementapp.dto.ReviewCreateRequest;
import com.bookmanagement.bookmanagementapp.dto.ReviewUpdateRequest;
import com.bookmanagement.bookmanagementapp.entity.Author;
import com.bookmanagement.bookmanagementapp.entity.Book;
import com.bookmanagement.bookmanagementapp.entity.Review;
import com.bookmanagement.bookmanagementapp.entity.Role;
import com.bookmanagement.bookmanagementapp.entity.User;
import com.bookmanagement.bookmanagementapp.exception.DuplicateResourceException;
import com.bookmanagement.bookmanagementapp.repository.BookRepository;
import com.bookmanagement.bookmanagementapp.repository.ReviewRepository;
import com.bookmanagement.bookmanagementapp.repository.UserRepository;
import com.bookmanagement.bookmanagementapp.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;

    private ReviewServiceImpl reviewService;

    private Book book;
    private User user;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewServiceImpl(reviewRepository, bookRepository, userRepository, new com.bookmanagement.bookmanagementapp.util.mapper.ApiMapper());
        Author author = new Author();
        author.setId(1L);
        author.setName("Morgan Housel");
        author.setEmail("morgan@example.com");

        book = new Book();
        book.setId(5L);
        book.setTitle("The Psychology of Money");
        book.setIsbn("9780857197689");
        book.setPublishedDate(LocalDate.of(2020, 9, 8));
        book.setAuthor(author);
        book.setRating(BigDecimal.ZERO);

        user = new User();
        user.setId(7L);
        user.setUsername("reader1");
        user.setEmail("reader1@example.com");
        user.setPassword("encoded");
        user.setRole(Role.USER);
    }

    @Test
    void createReviewShouldRejectDuplicateReviewPerUser() {
        when(bookRepository.findById(5L)).thenReturn(Optional.of(book));
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(reviewRepository.existsByBookIdAndUserId(5L, 7L)).thenReturn(true);

        assertThatThrownBy(() -> reviewService.createReview(5L, new ReviewCreateRequest(5, "Great", 7L)))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("User has already reviewed this book");
    }

    @Test
    void updateReviewShouldRecalculateBookRating() {
        Review review = new Review();
        review.setId(20L);
        review.setBook(book);
        review.setUser(user);
        review.setRating(4);
        review.setComment("Solid");
        review.setCreatedAt(LocalDateTime.now());

        when(reviewRepository.findById(20L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookRepository.findById(5L)).thenReturn(Optional.of(book));
        when(reviewRepository.findAverageRatingByBookId(5L)).thenReturn(4.5);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = reviewService.updateReview(20L, new ReviewUpdateRequest(5, "Excellent"));

        assertThat(response.rating()).isEqualTo(5);
        assertThat(book.getRating()).isEqualTo(new BigDecimal("4.50"));
        verify(bookRepository).save(book);
    }

    @Test
    void getReviewsByBookShouldReturnMappedResponses() {
        Review review = new Review();
        review.setId(20L);
        review.setBook(book);
        review.setUser(user);
        review.setRating(4);
        review.setComment("Solid");
        review.setCreatedAt(LocalDateTime.now());

        when(bookRepository.findById(5L)).thenReturn(Optional.of(book));
        when(reviewRepository.findAllByBookIdOrderByCreatedAtDesc(5L)).thenReturn(List.of(review));

        var responses = reviewService.getReviewsByBook(5L);

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().user().username()).isEqualTo("reader1");
    }
}
