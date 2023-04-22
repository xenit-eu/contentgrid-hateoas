package com.contentgrid.hateoas.pagination.api;

public interface PaginationControls extends Pagination {

    boolean hasNext();

    Pagination next();

    boolean hasPrevious();

    Pagination previous();

    Pagination first();

    static PaginationControls unpaged() {
        return Unpaged.instance();
    }
}
