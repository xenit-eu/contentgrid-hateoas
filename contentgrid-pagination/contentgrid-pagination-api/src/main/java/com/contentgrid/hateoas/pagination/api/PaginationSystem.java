package com.contentgrid.hateoas.pagination.api;

public interface PaginationSystem {

    boolean matches(PaginationParameters parameters);

    Pagination create(PaginationParameters parameters);

    /**
     * Defines the order in which this paginatinon-system is considered. Lower values have higher priority.
     *
     * <p>Default is {@literal 0}.
     *
     * @return the priority value
     */
    default int getPriority() {
        return 0;
    }
}
