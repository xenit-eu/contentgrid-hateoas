package com.contentgrid.hateoas.pagination.offset;

import com.contentgrid.hateoas.pagination.api.Pagination;
import com.contentgrid.hateoas.pagination.api.PaginationControls;
import java.util.Optional;
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
    public Optional<Pagination> next() {
        if (!this.hasNext) {
            return Optional.empty();
        }

        var next = OffsetPagination.offset(this.offset + this.pageSize, this.pageSize);
        return Optional.of(next);
    }

    @Override
    public boolean hasPrevious() {
        return this.offset > 0L;
    }

    @Override
    public Optional<Pagination> previous() {
        if (this.current().isFirstPage()) {
            return Optional.empty();
        }

        var previous = OffsetPagination.offset(Math.max(this.offset - this.pageSize, 0), this.pageSize);
        return Optional.of(previous);
    }

    @Override
    public Pagination first() {
        return OffsetPagination.offset(0, this.pageSize);
    }
}
