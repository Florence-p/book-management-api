package com.bookmanagement.bookmanagementapp.repository;

import com.bookmanagement.bookmanagementapp.entity.Book;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class BookSpecifications {

    private BookSpecifications() {
    }

    public static Specification<Book> withFilters(
            Long authorId,
            Long categoryId,
            Double ratingMin,
            Double ratingMax,
            LocalDate publishedStart,
            LocalDate publishedEnd
    ) {
        return Specification.allOf(
                hasAuthor(authorId),
                hasCategory(categoryId),
                ratingAtLeast(ratingMin),
                ratingAtMost(ratingMax),
                publishedOnOrAfter(publishedStart),
                publishedOnOrBefore(publishedEnd)
        );
    }

    private static Specification<Book> hasAuthor(Long authorId) {
        return (root, query, builder) ->
                authorId == null ? null : builder.equal(root.get("author").get("id"), authorId);
    }

    private static Specification<Book> hasCategory(Long categoryId) {
        return (root, query, builder) -> {
            if (categoryId == null) {
                return null;
            }
            query.distinct(true);
            return builder.equal(root.joinSet("categories").get("id"), categoryId);
        };
    }

    private static Specification<Book> ratingAtLeast(Double ratingMin) {
        return (root, query, builder) ->
                ratingMin == null ? null : builder.greaterThanOrEqualTo(root.get("rating"), BigDecimal.valueOf(ratingMin));
    }

    private static Specification<Book> ratingAtMost(Double ratingMax) {
        return (root, query, builder) ->
                ratingMax == null ? null : builder.lessThanOrEqualTo(root.get("rating"), BigDecimal.valueOf(ratingMax));
    }

    private static Specification<Book> publishedOnOrAfter(LocalDate publishedStart) {
        return (root, query, builder) ->
                publishedStart == null ? null : builder.greaterThanOrEqualTo(root.get("publishedDate"), publishedStart);
    }

    private static Specification<Book> publishedOnOrBefore(LocalDate publishedEnd) {
        return (root, query, builder) ->
                publishedEnd == null ? null : builder.lessThanOrEqualTo(root.get("publishedDate"), publishedEnd);
    }
}
