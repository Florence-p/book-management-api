package com.bookmanagement.bookmanagementapp.service;

import com.bookmanagement.bookmanagementapp.dto.*;
import com.bookmanagement.bookmanagementapp.entity.Author;
import com.bookmanagement.bookmanagementapp.exception.ResourceNotFoundException;
import com.bookmanagement.bookmanagementapp.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public List<AuthorSummaryResponse> getAllAuthors() {
        return authorRepository.findAll()
                .stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AuthorDetailResponse getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        return mapToDetailResponse(author);
    }

    @Override
    public AuthorSummaryResponse createAuthor(AuthorRequest request) {
        Author author = new Author();
        author.setName(request.getName());
        author.setEmail(request.getEmail());
        author.setBio(request.getBio());
        Author saved = authorRepository.save(author);
        return mapToSummaryResponse(saved);
    }

    @Override
    public AuthorSummaryResponse updateAuthor(Long id, AuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        author.setName(request.getName());
        author.setEmail(request.getEmail());
        author.setBio(request.getBio());
        Author updated = authorRepository.save(author);
        return mapToSummaryResponse(updated);
    }

    @Override
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        authorRepository.delete(author);
    }

    private AuthorSummaryResponse mapToSummaryResponse(Author author) {
        List<BookSummaryDto> books = author.getBooks() == null ? List.of() :
                author.getBooks().stream()
                        .map(book -> BookSummaryDto.builder()
                                .id(book.getId())
                                .title(book.getTitle())
                                .build())
                        .collect(Collectors.toList());

        return AuthorSummaryResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .email(author.getEmail())
                .bio(author.getBio())
                .books(books)
                .build();
    }

    private AuthorDetailResponse mapToDetailResponse(Author author) {
        List<BookDetailDto> books = author.getBooks() == null ? List.of() :
                author.getBooks().stream()
                        .map(book -> BookDetailDto.builder()
                                .id(book.getId())
                                .title(book.getTitle())
                                .isbn(book.getIsbn())
                                .publishedDate(book.getPublishedDate())
                                .build())
                        .collect(Collectors.toList());

        return AuthorDetailResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .email(author.getEmail())
                .bio(author.getBio())
                .books(books)
                .build();
    }
}