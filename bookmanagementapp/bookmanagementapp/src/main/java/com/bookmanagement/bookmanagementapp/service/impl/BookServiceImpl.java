package com.bookmanagement.bookmanagementapp.service.impl;

import com.bookmanagement.bookmanagementapp.dto.BookDetailResponse;
import com.bookmanagement.bookmanagementapp.dto.BookRequest;
import com.bookmanagement.bookmanagementapp.dto.BookResponse;
import com.bookmanagement.bookmanagementapp.entity.Author;
import com.bookmanagement.bookmanagementapp.entity.Book;
import com.bookmanagement.bookmanagementapp.entity.Category;
import com.bookmanagement.bookmanagementapp.exception.BadRequestException;
import com.bookmanagement.bookmanagementapp.exception.DuplicateResourceException;
import com.bookmanagement.bookmanagementapp.exception.ResourceNotFoundException;
import com.bookmanagement.bookmanagementapp.repository.AuthorRepository;
import com.bookmanagement.bookmanagementapp.repository.BookRepository;
import com.bookmanagement.bookmanagementapp.repository.BookSpecifications;
import com.bookmanagement.bookmanagementapp.repository.CategoryRepository;
import com.bookmanagement.bookmanagementapp.repository.ReviewRepository;
import com.bookmanagement.bookmanagementapp.service.BookService;
import com.bookmanagement.bookmanagementapp.util.mapper.ApiMapper;
import com.bookmanagement.bookmanagementapp.util.validation.IsbnUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    private final ApiMapper apiMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getAllBooks(
            Long authorId,
            Long categoryId,
            Double ratingMin,
            Double ratingMax,
            LocalDate publishedStart,
            LocalDate publishedEnd,
            Pageable pageable
    ) {
        validateBookFilterRange(ratingMin, ratingMax, publishedStart, publishedEnd);
        return bookRepository.findAll(
                BookSpecifications.withFilters(authorId, categoryId, ratingMin, ratingMax, publishedStart, publishedEnd),
                pageable
        ).map(apiMapper::toBookResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDetailResponse getBookById(Long id) {
        Book book = bookRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return apiMapper.toBookDetailResponse(book, reviewRepository.findAllByBookIdOrderByCreatedAtDesc(id));
    }

    @Override
    public BookResponse createBook(BookRequest request) {
        String normalizedIsbn = IsbnUtils.normalize(request.isbn());
        if (bookRepository.existsByIsbnIgnoreCase(normalizedIsbn)) {
            throw new DuplicateResourceException("ISBN already exists");
        }

        Book book = new Book();
        applyBookChanges(book, request, normalizedIsbn);
        return apiMapper.toBookResponse(bookRepository.save(book));
    }

    @Override
    public BookDetailResponse updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        String normalizedIsbn = IsbnUtils.normalize(request.isbn());
        if (bookRepository.existsByIsbnIgnoreCaseAndIdNot(normalizedIsbn, id)) {
            throw new DuplicateResourceException("ISBN already exists");
        }

        applyBookChanges(book, request, normalizedIsbn);
        Book updatedBook = bookRepository.save(book);
        return apiMapper.toBookDetailResponse(updatedBook, reviewRepository.findAllByBookIdOrderByCreatedAtDesc(updatedBook.getId()));
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        bookRepository.delete(book);
    }

    private void applyBookChanges(Book book, BookRequest request, String normalizedIsbn) {
        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + request.authorId()));
        Set<Category> categories = new LinkedHashSet<>(categoryRepository.findAllById(request.categoryIds()));
        if (categories.isEmpty()) {
            throw new BadRequestException("At least one valid category is required");
        }
        if (categories.size() != request.categoryIds().size()) {
            throw new BadRequestException("One or more category ids are invalid");
        }

        book.setTitle(request.title().trim());
        book.setIsbn(normalizedIsbn);
        book.setPublishedDate(request.publishedDate());
        book.setAuthor(author);
        book.setCategories(categories);
    }

    private void validateBookFilterRange(Double ratingMin, Double ratingMax, LocalDate publishedStart, LocalDate publishedEnd) {
        if (ratingMin != null && (ratingMin < 0 || ratingMin > 5)) {
            throw new BadRequestException("ratingMin must be between 0 and 5");
        }
        if (ratingMax != null && (ratingMax < 0 || ratingMax > 5)) {
            throw new BadRequestException("ratingMax must be between 0 and 5");
        }
        if (ratingMin != null && ratingMax != null && ratingMin > ratingMax) {
            throw new BadRequestException("ratingMin cannot be greater than ratingMax");
        }
        if (publishedStart != null && publishedEnd != null && publishedStart.isAfter(publishedEnd)) {
            throw new BadRequestException("publishedStart cannot be after publishedEnd");
        }
    }
}
