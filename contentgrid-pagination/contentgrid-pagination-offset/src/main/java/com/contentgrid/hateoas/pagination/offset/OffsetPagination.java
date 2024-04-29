package com.contentgrid.hateoas.pagination.offset;

import com.contentgrid.hateoas.pagination.api.Pagination;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface OffsetPagination extends Pagination {

    /**
     * Returns the page to be returned.
     *
     * @return the page to be returned.
     */
    int getPageNumber();

    /**
     * Returns the offset to be taken according to the underlying page and page size.
     *
     * @return the offset to be taken
     */
    long getOffset();

    /**
     * @return the maximum number of items on a page. May be {@literal null}
     */
    Integer getPageSize();

    default boolean isFirstPage() {
        return this.getOffset() == 0;
    }

    default Map<String, Object> getParameters() {
        return Map.of(
                "limit", this.getLimit(),
                "offset", this.getOffset()
        );
    }

    static OffsetPagination offset(long offset, Integer limit) {
        return new DefaultOffsetPagination(offset, limit);
    }

    static OffsetPagination firstPage() {
        return OffsetPagination.offset(0, null);
    }

    default OffsetPagination withLimit(int limit) {
        return OffsetPagination.offset(this.getOffset(), limit);
    }


    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    class DefaultOffsetPagination implements OffsetPagination {

        @Getter
        private final long offset;

        private final Integer pageSize;

        @Override
        public Integer getLimit() {
            return this.pageSize;
        }

        @Override
        public Optional<Long> getReference() {
            return Optional.of(this.offset);
        }

        @Override
        public int getPageNumber() {
            return (int) (offset / pageSize);
        }

        @Override
        public Integer getPageSize() {
            return this.pageSize;
        }
    }

}
