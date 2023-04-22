package com.contentgrid.hateoas.pagination.api;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import lombok.NonNull;

/**
 * A part of a result-set of items, with a pointer to access the next part of the result-set
 *
 * @param <T> The type of the items in this slice
 */
public interface Slice<T> extends Iterable<T> {

    /**
     * Returns the slice content as a {@link List}.
     *
     * @return the slice content as a {@link List}.
     */
    List<T> getContent();

    /**
     * @return the {@link Pagination} that was used for retrieving this slice
     */
    PaginationControls getPagination();

    /**
     * Returns {@literal true} if there is a next {@link Slice}.
     *
     * @return {@literal true} if there is a next {@link Slice}.
     */
    boolean hasNext();

    Pagination nextPage();

    Pagination previousPage();

    Pagination firstPage();

    default Iterator<T> iterator() {
        return this.getContent().iterator();
    }


    static <T> Slice<T> empty() {
        return new DefaultSlice<>(List.of(), PaginationControls.unpaged());
    }

    static <T> Slice<T> empty(PaginationControls pagination) {
        return new DefaultSlice<>(List.of(), pagination);
    }

    static <T> Slice<T> from(List<T> contents, PaginationControls pagination) {
        return new DefaultSlice<>(contents, pagination);
    }

    default <U> Slice<U> map(Function<? super T, U> converter) {
        return Slice.from(
               this.getContent().stream().map(converter).toList(),
               this.getPagination()
        );
    }

    class DefaultSlice<T> implements Slice<T> {

        private final List<T> content;

        private final PaginationControls pagination;

        public DefaultSlice(@NonNull List<T> content, @NonNull PaginationControls pagination) {
            int effectiveLimit = pagination.getLimit() != null
                    ? Math.min(pagination.getLimit(), content.size())
                    : content.size();

            this.content = List.copyOf(content).subList(0, effectiveLimit);
            this.pagination = pagination;
        }


        public List<T> getContent() {
            return this.content;
        }

        public PaginationControls getPagination() {
            return this.pagination;
        }

        public boolean hasNext() {
            return this.pagination.hasNext();
        }

        @Override
        public Pagination nextPage() {
            return this.pagination.next();
        }

        @Override
        public Pagination previousPage() {
            return this.pagination.previous();
        }

        @Override
        public Pagination firstPage() {
            return this.pagination.first();
        }
    }

}
