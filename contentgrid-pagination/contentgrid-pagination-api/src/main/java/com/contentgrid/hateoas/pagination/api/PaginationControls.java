package com.contentgrid.hateoas.pagination.api;

public interface PaginationControls extends Pagination {

    /**
     * @return {@literal true} if there is a next page of data.
     */
    boolean hasNext();

    /**
     * Returns the {@link Pagination} to request the next page.
     * <p>
     * Can be {@link Pagination#unpaged()} or {@code this} instance, in case the current page is already the last one or
     * it is not possible to navigate to the next page. Clients should check {@link #hasNext()} before calling this
     * method.
     *
     * @return the {@link Pagination} to request the next page.
     */
    Pagination next();

    /**
     * Returns {@literal true} if there is data before the current page and if it is possible to navigate backwards.
     *
     * @return {@literal true} if it is possible to navigate to a previous page.
     */
    boolean hasPrevious();

    /**
     * Returns the {@link Pagination} to request the previous page.
     * <p>
     * Can be {@link Pagination#unpaged()} or {@code this} instance, in case the current page is already the first page,
     * or it is not possible to navigate backwards. Clients should check {@link #hasPrevious()} before calling this
     * method.
     *
     * @return the {@link Pagination} to request the previous page.
     */
    Pagination previous();

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
