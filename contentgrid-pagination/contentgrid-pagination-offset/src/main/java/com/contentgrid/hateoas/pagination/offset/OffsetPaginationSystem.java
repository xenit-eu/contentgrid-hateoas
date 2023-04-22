package com.contentgrid.hateoas.pagination.offset;

import com.contentgrid.hateoas.pagination.api.Pagination;
import com.contentgrid.hateoas.pagination.api.PaginationControls;
import com.contentgrid.hateoas.pagination.api.PaginationParameters;
import com.contentgrid.hateoas.pagination.api.PaginationSystem;
import lombok.NonNull;

public class OffsetPaginationSystem implements PaginationSystem {

    @Override
    public boolean matches(PaginationParameters parameters) {
        return parameters.containsKey("offset");
    }

    @Override
    public Pagination create(PaginationParameters parameters) {
        var offset = parameters.getLong("offset", 0L);
        var limit = parameters.getInteger("limit", null);

        return OffsetPagination.offset(offset, limit);
    }

    public static PaginationControls createPaginationControls(OffsetPagination pagination, boolean hasNext) {
        return new OffsetPaginationControls(pagination.getOffset(), pagination.getPageSize(), hasNext);
    }

    public static OffsetPagination convert(@NonNull Pagination pagination) {
        // no paging information at all
        if (pagination.isUnpaged()) {
            return OffsetPagination.firstPage();
        }

        // lucky, this is already an OffsetPagination
        if (pagination instanceof OffsetPagination offset) {
            return offset;
        }

        // fallback to first-page with given limit
        return OffsetPagination.offset(0L, pagination.getLimit());
    }
}
