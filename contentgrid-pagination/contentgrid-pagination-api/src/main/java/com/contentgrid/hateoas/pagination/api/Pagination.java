package com.contentgrid.hateoas.pagination.api;

import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

public interface Pagination {

    /**
     * @return the maximum number of items that should be returned. May be {@literal null}
     */
    Integer getLimit();

    Optional<?> getReference();

    boolean isFirstPage();

    Map<String, Object> getParameters();

    default boolean isUnpaged() {
        return false;
    }

    default boolean isLimited() {
        return this.getLimit() != null;
    }

    static Pagination unpaged() {
        return Unpaged.instance();
    }

    static Pagination firstPage() {
        return Pagination.unpaged();
    }

    static Pagination firstPage(int size) {
        return limit(size);
    }

    static Pagination limit(int size) {
        return new PageLimit(size);
    }

    static Pagination from(PaginationParameters parameters) {
        return PaginationSystemLoader.select(parameters)
                .map(system -> system.create(parameters))
                .orElse(Pagination.unpaged());
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class PageLimit implements Pagination {

        private final int limit;

        @Override
        public Integer getLimit() {
            return this.limit;
        }

        @Override
        public Optional getReference() {
            return Optional.empty();
        }

        @Override
        public boolean isFirstPage() {
            return true;
        }

        @Override
        public Map<String, Object> getParameters() {
            return Map.of("limit", this.limit);
        }
    }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class Unpaged implements PaginationControls {

        static Unpaged instance() {
            return new Unpaged();
        }

        @Override
        public Integer getLimit() {
            return null;
        }

        @Override
        public Optional getReference() {
            return Optional.empty();
        }

        @Override
        public boolean isFirstPage() {
            return true;
        }

        @Override
        public Map<String, Object> getParameters() {
            return Map.of();
        }

        public boolean isPaged() {
            return false;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Pagination next() {
            return this;
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }

        @Override
        public Pagination previous() {
            return this;
        }

        @Override
        public Pagination first() {
            return this;
        }
    }

}
