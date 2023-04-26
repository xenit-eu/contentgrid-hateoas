package com.contentgrid.hateoas.pagination.api;

import java.util.Iterator;
import java.util.List;
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

    /**
     * Returns the number of elements currently on this {@link Slice}.
     *
     * @return the number of elements currently on this {@link Slice}.
     */
    default int getSize() {
        return this.getContent().size();
    }

    /**
     * @return the maximum number of items in this {@link Slice}. May be {@literal null}.
     */
    default Integer getLimit() {
        return this.getPagination().getLimit();
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

        private final PaginationControls controls;

        public DefaultSlice(@NonNull List<T> content, @NonNull PaginationControls controls) {
            int effectiveLimit = controls.current().getLimit() != null
                    ? Math.min(controls.current().getLimit(), content.size())
                    : content.size();

            this.content = List.copyOf(content).subList(0, effectiveLimit);
            this.controls = controls;
        }


        public List<T> getContent() {
            return this.content;
        }

        public Pagination getPagination() {
            return this.controls.current();
        }

        @Override
        public Pagination current() {
            return this.controls.current();
        }

        @Override
        public Optional<Pagination> next() {
            return this.controls.next();
        }

        @Override
        public Optional<Pagination> previous() {
            return this.controls.previous();
        }

        @Override
        public Pagination first() {
            return this.controls.first();
        }
    }

}
