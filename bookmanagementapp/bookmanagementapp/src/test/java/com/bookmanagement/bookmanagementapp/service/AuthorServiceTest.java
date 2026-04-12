package com.bookmanagement.bookmanagementapp.service;

import com.bookmanagement.bookmanagementapp.dto.AuthorRequest;
import com.bookmanagement.bookmanagementapp.entity.Author;
import com.bookmanagement.bookmanagementapp.exception.DuplicateResourceException;
import com.bookmanagement.bookmanagementapp.repository.AuthorRepository;
import com.bookmanagement.bookmanagementapp.repository.BookRepository;
import com.bookmanagement.bookmanagementapp.service.impl.AuthorServiceImpl;
import com.bookmanagement.bookmanagementapp.util.mapper.ApiMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    private AuthorServiceImpl authorService;

    @BeforeEach
    void setUp() {
        authorService = new AuthorServiceImpl(authorRepository, bookRepository, new ApiMapper());
    }

    @Test
    void createAuthorShouldRejectDuplicateEmail() {
        AuthorRequest request = new AuthorRequest("Ben Graham", "ben@example.com", "Legendary value investor");
        when(authorRepository.existsByEmailIgnoreCase("ben@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authorService.createAuthor(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Author email already exists");
    }

    @Test
    void updateAuthorShouldPersistTrimmedValues() {
        Author author = new Author();
        author.setId(10L);
        author.setName("Old Name");
        author.setEmail("old@example.com");

        when(authorRepository.findById(10L)).thenReturn(Optional.of(author));
        when(authorRepository.save(any(Author.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = authorService.updateAuthor(10L, new AuthorRequest("  Benjamin Graham  ", "BEN@EXAMPLE.COM", "Investor"));

        assertThat(response.name()).isEqualTo("Benjamin Graham");
        assertThat(response.email()).isEqualTo("ben@example.com");
    }
}
