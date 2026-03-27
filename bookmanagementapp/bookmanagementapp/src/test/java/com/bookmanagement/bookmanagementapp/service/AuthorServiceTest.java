package com.bookmanagement.bookmanagementapp.service;

import com.bookmanagement.bookmanagementapp.dto.AuthorDetailResponse;
import com.bookmanagement.bookmanagementapp.dto.AuthorRequest;
import com.bookmanagement.bookmanagementapp.dto.AuthorSummaryResponse;
import com.bookmanagement.bookmanagementapp.entity.Author;
import com.bookmanagement.bookmanagementapp.exception.ResourceNotFoundException;
import com.bookmanagement.bookmanagementapp.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private Author author;
    private AuthorRequest authorRequest;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);
        author.setName("Chimamanda Ngozi Adichie");
        author.setEmail("chimamanda@authors.com");
        author.setBio("Award-winning Nigerian author.");
        author.setBooks(new ArrayList<>());

        authorRequest = new AuthorRequest(
                "Chimamanda Ngozi Adichie",
                "chimamanda@authors.com",
                "Award-winning Nigerian author."
        );
    }

    @Test
    void shouldReturnAllAuthors() {
        when(authorRepository.findAll()).thenReturn(List.of(author));

        List<AuthorSummaryResponse> responses = authorService.getAllAuthors();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("Chimamanda Ngozi Adichie");
        verify(authorRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnAuthorById() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        AuthorDetailResponse response = authorService.getAuthorById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Chimamanda Ngozi Adichie");
        assertThat(response.getEmail()).isEqualTo("chimamanda@authors.com");
    }

    @Test
    void shouldThrowExceptionWhenAuthorNotFound() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.getAuthorById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Author not found with id: 99");
    }

    @Test
    void shouldCreateAuthorSuccessfully() {
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        AuthorSummaryResponse response = authorService.createAuthor(authorRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Chimamanda Ngozi Adichie");
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void shouldUpdateAuthorSuccessfully() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        AuthorSummaryResponse response = authorService.updateAuthor(1L, authorRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Chimamanda Ngozi Adichie");
    }

    @Test
    void shouldDeleteAuthorSuccessfully() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        doNothing().when(authorRepository).delete(author);

        authorService.deleteAuthor(1L);

        verify(authorRepository, times(1)).delete(author);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentAuthor() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.deleteAuthor(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Author not found with id: 99");
    }
}