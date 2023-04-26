package com.contentgrid.hateoas.pagination.api;

import com.contentgrid.hateoas.pagination.api.Pagination.Unpaged;
import java.util.Optional;

public interface PaginationControls {

    /**
     * @return the {@link Pagination} that was used to request the current page of data.
     */
    Pagination current();

    /**
     * @return {@literal true} if there is a next page of data.
     */
    default boolean hasNext() {
        return this.next().isPresent();
    }

    /**
     * Returns an {@link Optional} of {@link Pagination} to request the next page.
     * <p>
     * In case the current page is already the last page or it is not possible to navigate to the next page, the
     * returned value will be {@code Optional.empty()}
     *
     * @return an {@link Optional} of {@link Pagination} to request the next page.
     */
    Optional<Pagination> next();

    /**
     * Returns {@literal true} if there is data before the current page and if it is possible to navigate backwards.
     *
     * @return {@literal true} if it is possible to navigate to a previous page.
     */
    default boolean hasPrevious() {
        return this.previous().isPresent();
    }

    /**
     * Returns the {@link Pagination} to request the previous page.
     * <p>
     * In case the current page is already the first page, or it is not possible to navigate backwards.
     * Clients should check {@link #hasPrevious()} before calling this method.
     *
     * @return an {@link Optional} of {@link Pagination} to request the previous page.
     */
    Optional<Pagination> previous();

    /**
     * Returns the {@link Pagination} to request the first page.
     * <p>
     * Can be {@code this} instance in case the current page is already the first page.
     * Can be {@link Pagination#unpaged()} for some {@link PaginationSystem} implementation.
     *
     * @return the {@link Pagination} to request the first page.
     */
    Pagination first();

    /**
     * @return Returns a {@link PaginationControls} instance representing no pagination setup.
     */
    static PaginationControls unpaged() {
        return Unpaged.instance();
    }
}
