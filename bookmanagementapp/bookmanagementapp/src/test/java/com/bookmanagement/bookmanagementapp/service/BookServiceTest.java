package com.bookmanagement.bookmanagementapp.service;

import com.bookmanagement.bookmanagementapp.dto.BookRequest;
import com.bookmanagement.bookmanagementapp.entity.Author;
import com.bookmanagement.bookmanagementapp.entity.Book;
import com.bookmanagement.bookmanagementapp.entity.Category;
import com.bookmanagement.bookmanagementapp.exception.BadRequestException;
import com.bookmanagement.bookmanagementapp.exception.DuplicateResourceException;
import com.bookmanagement.bookmanagementapp.repository.AuthorRepository;
import com.bookmanagement.bookmanagementapp.repository.BookRepository;
import com.bookmanagement.bookmanagementapp.repository.CategoryRepository;
import com.bookmanagement.bookmanagementapp.repository.ReviewRepository;
import com.bookmanagement.bookmanagementapp.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ReviewRepository reviewRepository;

    private BookServiceImpl bookService;

    private Author author;
    private Category category;

    @BeforeEach
    void setUp() {
        bookService = new BookServiceImpl(bookRepository, authorRepository, categoryRepository, reviewRepository, new com.bookmanagement.bookmanagementapp.util.mapper.ApiMapper());
        author = new Author();
        author.setId(1L);
        author.setName("Robert Kiyosaki");
        author.setEmail("author@example.com");

        category = new Category();
        category.setId(2L);
        category.setName("Finance");
    }

    @Test
    void getAllBooksShouldRejectInvalidRatingRange() {
        assertThatThrownBy(() -> bookService.getAllBooks(null, null, 4.5, 3.0, null, null, PageRequest.of(0, 10)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("ratingMin cannot be greater than ratingMax");
    }

    @Test
    void createBookShouldRejectDuplicateIsbn() {
        when(bookRepository.existsByIsbnIgnoreCase("9780132350884")).thenReturn(true);

        assertThatThrownBy(() -> bookService.createBook(new BookRequest(
                "Clean Code",
                "978-0132350884",
                LocalDate.of(2008, 8, 1),
                1L,
                Set.of(2L)
        ))).isInstanceOf(DuplicateResourceException.class)
                .hasMessage("ISBN already exists");
    }

    @Test
    void createBookShouldSaveNormalizedIsbnAndCategories() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findAllById(Set.of(2L))).thenReturn(List.of(category));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book saved = invocation.getArgument(0);
            saved.setId(99L);
            saved.setRating(BigDecimal.ZERO);
            return saved;
        });

        var response = bookService.createBook(new BookRequest(
                "Clean Code",
                "978-0132350884",
                LocalDate.of(2008, 8, 1),
                1L,
                new LinkedHashSet<>(Set.of(2L))
        ));

        assertThat(response.id()).isEqualTo(99L);
        assertThat(response.isbn()).isEqualTo("9780132350884");
        assertThat(response.categories()).hasSize(1);
    }

    @Test
    void getAllBooksShouldMapPagedResults() {
        Book book = new Book();
        book.setId(11L);
        book.setTitle("The Intelligent Investor");
        book.setIsbn("9780060555665");
        book.setPublishedDate(LocalDate.of(1949, 1, 1));
        book.setAuthor(author);
        book.setCategories(new LinkedHashSet<>(Set.of(category)));
        book.setRating(new BigDecimal("4.25"));

        when(bookRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(book)));

        var result = bookService.getAllBooks(null, null, null, null, null, null, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().title()).isEqualTo("The Intelligent Investor");
    }
}
