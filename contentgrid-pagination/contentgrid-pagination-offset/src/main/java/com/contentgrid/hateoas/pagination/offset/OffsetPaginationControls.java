package com.contentgrid.hateoas.pagination.offset;

import com.contentgrid.hateoas.pagination.api.Pagination;
import com.contentgrid.hateoas.pagination.api.PaginationControls;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class OffsetPaginationControls implements OffsetPagination, PaginationControls {

    @Getter
    private final long offset;

    private final int pageSize;

    @Getter
    @Accessors(fluent = true)
    private final boolean hasNext;

    @Override
    public Integer getLimit() {
        return this.pageSize;
    }

    @Override
    public Optional<Long> getReference() {
        return Optional.of(this.getOffset());
    }

    @Override
    public boolean isFirstPage() {
        return this.getPageNumber() == 0;
    }


    @Override
    public int getPageNumber() {
        return (int) offset / pageSize;
    }

    @Override
    public Integer getPageSize() {
        return this.pageSize;
    }

    @Override
    public Pagination next() {
        return this.hasNext()
                ? OffsetPagination.offset(this.getOffset() + this.getPageSize(), this.getPageSize())
                : this;
    }

    @Override
    public boolean hasPrevious() {
        return this.offset > 0L;
    }

    @Override
    public Pagination previous() {
        return OffsetPagination.offset(Math.max(this.getOffset() - this.getPageSize(), 0), this.getPageSize());
    }

    @Override
    public Pagination first() {
        return OffsetPagination.offset(0, this.getPageSize());
    }
}
