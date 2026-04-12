package com.bookmanagement.bookmanagementapp.util.mapper;

import com.bookmanagement.bookmanagementapp.dto.AuthorDetailResponse;
import com.bookmanagement.bookmanagementapp.dto.AuthorSummaryDto;
import com.bookmanagement.bookmanagementapp.dto.AuthorSummaryResponse;
import com.bookmanagement.bookmanagementapp.dto.BookDetailDto;
import com.bookmanagement.bookmanagementapp.dto.BookDetailResponse;
import com.bookmanagement.bookmanagementapp.dto.BookResponse;
import com.bookmanagement.bookmanagementapp.dto.BookSummaryDto;
import com.bookmanagement.bookmanagementapp.dto.CategoryResponse;
import com.bookmanagement.bookmanagementapp.dto.ReviewResponse;
import com.bookmanagement.bookmanagementapp.dto.UserResponse;
import com.bookmanagement.bookmanagementapp.dto.UserSummaryDto;
import com.bookmanagement.bookmanagementapp.entity.Author;
import com.bookmanagement.bookmanagementapp.entity.Book;
import com.bookmanagement.bookmanagementapp.entity.Category;
import com.bookmanagement.bookmanagementapp.entity.Review;
import com.bookmanagement.bookmanagementapp.entity.User;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ApiMapper {

    public AuthorSummaryResponse toAuthorSummaryResponse(Author author) {
        return new AuthorSummaryResponse(
                author.getId(),
                author.getName(),
                author.getEmail(),
                author.getBio(),
                author.getBooks().stream()
                        .sorted(Comparator.comparing(Book::getTitle))
                        .map(this::toBookSummaryDto)
                        .toList()
        );
    }

    public AuthorDetailResponse toAuthorDetailResponse(Author author) {
        return new AuthorDetailResponse(
                author.getId(),
                author.getName(),
                author.getEmail(),
                author.getBio(),
                author.getBooks().stream()
                        .sorted(Comparator.comparing(Book::getTitle))
                        .map(this::toBookDetailDto)
                        .toList()
        );
    }

    public BookResponse toBookResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublishedDate(),
                toAuthorSummaryDto(book.getAuthor()),
                toCategoryResponses(book.getCategories().stream().toList()),
                book.getRating()
        );
    }

    public BookDetailResponse toBookDetailResponse(Book book, List<Review> reviews) {
        return new BookDetailResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublishedDate(),
                new BookDetailResponse.AuthorDetailDto(
                        book.getAuthor().getId(),
                        book.getAuthor().getName(),
                        book.getAuthor().getEmail(),
                        book.getAuthor().getBio()
                ),
                toCategoryResponses(book.getCategories().stream().toList()),
                book.getRating(),
                reviews.stream().map(this::toReviewResponse).toList()
        );
    }

    public CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }

    public List<CategoryResponse> toCategoryResponses(List<Category> categories) {
        return categories.stream()
                .sorted(Comparator.comparing(Category::getName))
                .map(this::toCategoryResponse)
                .toList();
    }

    public ReviewResponse toReviewResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                toBookSummaryDto(review.getBook()),
                toUserSummaryDto(review.getUser()),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }

    public UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }

    private AuthorSummaryDto toAuthorSummaryDto(Author author) {
        return new AuthorSummaryDto(author.getId(), author.getName());
    }

    private UserSummaryDto toUserSummaryDto(User user) {
        return new UserSummaryDto(user.getId(), user.getUsername());
    }

    private BookSummaryDto toBookSummaryDto(Book book) {
        return new BookSummaryDto(book.getId(), book.getTitle());
    }

    private BookDetailDto toBookDetailDto(Book book) {
        return new BookDetailDto(book.getId(), book.getTitle(), book.getIsbn(), book.getPublishedDate());
    }
}
