package com.contentgrid.hateoas.pagination.api;

public interface PaginationControls extends Pagination {

    /**
     * @return {@literal true} if there is a next page of data.
     */
    boolean hasNext();

    Pagination next();

    boolean hasPrevious();

    Pagination previous();

    Pagination first();

    static PaginationControls unpaged() {
        return Unpaged.instance();
    }
}
