package com.contentgrid.hateoas.pagination.offset;

import com.contentgrid.hateoas.pagination.api.Pagination;
import com.contentgrid.hateoas.pagination.api.PaginationControls;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class OffsetPaginationControls implements PaginationControls {

    private final long offset;
    private final int pageSize;

    @Getter
    @Accessors(fluent = true)
    private final boolean hasNext;

    @Override
    public OffsetPagination current() {
        return OffsetPagination.offset(this.offset, this.pageSize);
    }

    @Override
    public Pagination next() {
        return this.hasNext()
                ? OffsetPagination.offset(this.offset + this.pageSize, this.pageSize)
                : this.current();
    }

    @Override
    public boolean hasPrevious() {
        return this.offset > 0L;
    }

    @Override
    public Pagination previous() {
        return OffsetPagination.offset(Math.max(this.offset - this.pageSize, 0), this.pageSize);
    }

    @Override
    public Pagination first() {
        return OffsetPagination.offset(0, this.pageSize);
    }
}
