package com.bookmanagement.bookmanagementapp.service.impl;

import com.bookmanagement.bookmanagementapp.dto.AuthorDetailResponse;
import com.bookmanagement.bookmanagementapp.dto.AuthorRequest;
import com.bookmanagement.bookmanagementapp.dto.AuthorSummaryResponse;
import com.bookmanagement.bookmanagementapp.entity.Author;
import com.bookmanagement.bookmanagementapp.exception.BadRequestException;
import com.bookmanagement.bookmanagementapp.exception.DuplicateResourceException;
import com.bookmanagement.bookmanagementapp.exception.ResourceNotFoundException;
import com.bookmanagement.bookmanagementapp.repository.AuthorRepository;
import com.bookmanagement.bookmanagementapp.repository.BookRepository;
import com.bookmanagement.bookmanagementapp.service.AuthorService;
import com.bookmanagement.bookmanagementapp.util.mapper.ApiMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final ApiMapper apiMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AuthorSummaryResponse> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(apiMapper::toAuthorSummaryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorDetailResponse getAuthorById(Long id) {
        return apiMapper.toAuthorDetailResponse(getAuthor(id));
    }

    @Override
    public AuthorDetailResponse createAuthor(AuthorRequest request) {
        if (authorRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("Author email already exists");
        }

        Author author = new Author();
        author.setName(request.name().trim());
        author.setEmail(request.email().trim().toLowerCase());
        author.setBio(request.bio());

        return apiMapper.toAuthorDetailResponse(authorRepository.save(author));
    }

    @Override
    public AuthorDetailResponse updateAuthor(Long id, AuthorRequest request) {
        Author author = getAuthor(id);
        if (authorRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            throw new DuplicateResourceException("Author email already exists");
        }

        author.setName(request.name().trim());
        author.setEmail(request.email().trim().toLowerCase());
        author.setBio(request.bio());

        return apiMapper.toAuthorDetailResponse(authorRepository.save(author));
    }

    @Override
    public void deleteAuthor(Long id) {
        if (bookRepository.existsByAuthorId(id)) {
            throw new BadRequestException("Author cannot be deleted because books are still assigned");
        }
        authorRepository.delete(getAuthor(id));
    }

    private Author getAuthor(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
    }
}
