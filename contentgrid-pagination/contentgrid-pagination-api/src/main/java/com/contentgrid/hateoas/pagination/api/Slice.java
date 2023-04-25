package com.contentgrid.hateoas.pagination.api;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.NonNull;

/**
 * A part of a result-set of items, with a pointer to access the next part of the result-set
 *
 * @param <T> The type of the items in this slice
 */
public interface Slice<T> extends Iterable<T>, PaginationControls {

    /**
     * Returns the slice content as a {@link List}.
     *
     * @return the slice content as a {@link List}.
     */
    List<T> getContent();

    /**
     * @return the {@link Pagination} that was used to request the current {@link Slice}.
     */
    Pagination getPagination();

    default Iterator<T> iterator() {
        return this.getContent().iterator();
    }

    @Override
    default Integer getLimit() {
        return this.getPagination().getLimit();
    }

    @Override
    default Optional<?> getReference() {
        return this.getPagination().getReference();
    }

    @Override
    default boolean isFirstPage() {
        return this.getPagination().isFirstPage();
    }

    @Override
    default Map<String, Object> getParameters() {
        return this.getPagination().getParameters();
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
               this
        );
    }

    class DefaultSlice<T> implements Slice<T> {

        private final List<T> content;

        private final PaginationControls paginationControls;

        public DefaultSlice(@NonNull List<T> content, @NonNull PaginationControls pagination) {
            int effectiveLimit = pagination.getLimit() != null
                    ? Math.min(pagination.getLimit(), content.size())
                    : content.size();

            this.content = List.copyOf(content).subList(0, effectiveLimit);
            this.paginationControls = pagination;
        }


        public List<T> getContent() {
            return this.content;
        }

        public Pagination getPagination() {
            return this.paginationControls;
        }

        public boolean hasNext() {
            return this.paginationControls.hasNext();
        }

        @Override
        public Pagination next() {
            return this.paginationControls.next();
        }

        @Override
        public boolean hasPrevious() {
            return this.paginationControls.hasPrevious();
        }

        @Override
        public Pagination previous() {
            return this.paginationControls.previous();
        }

        @Override
        public Pagination first() {
            return this.paginationControls.first();
        }
    }

}
