package com.bookmanagement.bookmanagementapp.service;

import com.bookmanagement.bookmanagementapp.dto.BookRequest;
import com.bookmanagement.bookmanagementapp.dto.BookResponse;
import com.bookmanagement.bookmanagementapp.dto.BookDetailResponse;
import com.bookmanagement.bookmanagementapp.entity.Author;
import com.bookmanagement.bookmanagementapp.entity.Book;
import com.bookmanagement.bookmanagementapp.entity.Category;
import com.bookmanagement.bookmanagementapp.exception.ResourceNotFoundException;
import com.bookmanagement.bookmanagementapp.repository.AuthorRepository;
import com.bookmanagement.bookmanagementapp.repository.BookRepository;
import com.bookmanagement.bookmanagementapp.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private Author author;
    private Category category;
    private BookRequest bookRequest;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);
        author.setName("Chimamanda Ngozi Adichie");
        author.setEmail("chimamanda@authors.com");
        author.setBio("Award-winning Nigerian author.");

        category = new Category();
        category.setId(1L);
        category.setName("African Literature");

        book = new Book();
        book.setId(1L);
        book.setTitle("Half of a Yellow Sun");
        book.setIsbn("9780007200283");
        book.setPublishedDate(LocalDate.of(2006, 9, 12));
        book.setAuthor(author);
        book.setCategories(List.of(category));
        book.setReviews(new ArrayList<>());
        book.setRating(0.0);

        bookRequest = new BookRequest(
                "Half of a Yellow Sun",
                "9780007200283",
                LocalDate.of(2006, 9, 12),
                1L,
                List.of(1L)
        );
    }

    @Test
    void shouldGetAllBooks() {
        Page<Book> bookPage = new PageImpl<>(List.of(book));
        when(bookRepository.findAll(any(PageRequest.class))).thenReturn(bookPage);

        Page<BookResponse> responses = bookService.getAllBooks(
                null, null, null, null, null, null, PageRequest.of(0, 10));

        assertThat(responses.getContent()).hasSize(1);
        assertThat(responses.getContent().get(0).getTitle()).isEqualTo("Half of a Yellow Sun");
    }

    @Test
    void shouldGetBookById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookDetailResponse response = bookService.getBookById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Half of a Yellow Sun");
        assertThat(response.getAuthor().getName()).isEqualTo("Chimamanda Ngozi Adichie");
    }

    @Test
    void shouldThrowExceptionWhenBookNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book not found with id: 99");
    }

    @Test
    void shouldCreateBookSuccessfully() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponse response = bookService.createBook(bookRequest);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Half of a Yellow Sun");
        assertThat(response.getAuthor().getName()).isEqualTo("Chimamanda Ngozi Adichie");
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void shouldThrowExceptionWhenAuthorNotFoundOnCreate() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());
        bookRequest = new BookRequest(
                "Half of a Yellow Sun",
                "9780007200283",
                LocalDate.of(2006, 9, 12),
                99L,
                List.of(1L)
        );

        assertThatThrownBy(() -> bookService.createBook(bookRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Author not found with id: 99");
    }

    @Test
    void shouldDeleteBookSuccessfully() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).delete(book);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).delete(book);
    }
}