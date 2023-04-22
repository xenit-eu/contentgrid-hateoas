package com.contentgrid.hateoas.spring.pagination;

import java.util.Collection;
import java.util.List;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.lang.Nullable;

public class SlicedModel<T> extends CollectionModel<T> {

    private final PaginationMetadata metadata;

    protected SlicedModel(Collection<T> content, @Nullable PaginationMetadata metadata, Iterable<Link> links) {
        super(content, links, null);

        this.metadata = metadata;
    }

    public static <T> SlicedModel<T> of(Collection<T> content, @Nullable PaginationMetadata metadata) {
        return new SlicedModel<>(content, metadata, List.of());
    }



}
