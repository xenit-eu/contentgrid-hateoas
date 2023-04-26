package com.contentgrid.hateoas.spring.pagination;

import com.contentgrid.hateoas.pagination.api.Pagination;
import com.contentgrid.hateoas.pagination.api.PaginationControls;
import com.contentgrid.hateoas.pagination.api.Slice;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
public class SlicedResourcesAssembler<T> implements RepresentationModelAssembler<Slice<T>, SlicedModel<?>> {

    private final PaginationHandlerMethodArgumentResolver paginationResolver;

    @Override
    public SlicedModel<EntityModel<T>> toModel(Slice<T> slice) {
        return createModel(slice.map(EntityModel::of), Optional.empty());
    }

    public <R extends RepresentationModel<?>> SlicedModel<R> toModel(Slice<T> slice,
            RepresentationModelAssembler<T, R> assembler, Optional<Link> selfLink) {
        return createModel(slice.map(assembler::toModel), selfLink);
    }

    public <S extends RepresentationModel<?>> SlicedModel<S> of(@NonNull Slice<S> slice) {
        return this.createModel(slice);
    }

    private <S extends RepresentationModel<?>> SlicedModel<S> createModel(@NonNull Slice<S> slice)
    {
        return this.createModel(slice, Optional.empty());
    }

    private <S extends RepresentationModel<?>> SlicedModel<S> createModel(@NonNull Slice<S> slice,
            /*@NonNull RepresentationModelAssembler<S, R> assembler, */
            Optional<Link> link) {

        Assert.notNull(slice, "Slice must not be null");
//        Assert.notNull(assembler, "ResourceAssembler must not be null");

        // List<R> resources = new ArrayList<>(page.getNumberOfElements());

//        var content = slice.map(assembler::toModel).getContent();
        var metadata = asSliceMetadata(slice);
        var model = SlicedModel.of(slice.getContent(), metadata);

        return addPaginationLinks(model, slice, link);
    }

    private PaginationMetadata asSliceMetadata(@NonNull Slice<?> slice) {
        return new PaginationMetadata(slice.getPagination().getLimit(), null);
    }

    private <R> SlicedModel<R> addPaginationLinks(SlicedModel<R> model, PaginationControls controls, Optional<Link> link) {
        UriTemplate base = getUriTemplate(link);

        boolean isNavigable = controls.hasPrevious() || controls.hasNext();

        if (isNavigable) {
            model.add(createLink(base, controls.first(), IanaLinkRelations.FIRST));
        }

        if (controls.hasPrevious()) {
            model.add(createLink(base, controls.previous(), IanaLinkRelations.PREV));
        }

        Link selfLink = link.map(Link::withSelfRel)//
                .orElseGet(() -> createLink(base, controls.current(), IanaLinkRelations.SELF));

        model.add(selfLink);

        if (controls.hasNext()) {
            model.add(createLink(base, controls.next(), IanaLinkRelations.NEXT));
        }

        return model;
    }

    /**
     * Creates a {@link Link} with the given {@link LinkRelation} that will be based on the given {@link UriTemplate} but
     * enriched with the values of the given {@link Pagination} (if not {@literal null}).
     *
     * @param base must not be {@literal null}.
     * @param pagination can be {@literal null}
     * @param relation must not be {@literal null}.
     * @return
     */
    private Link createLink(UriTemplate base, Pagination pagination, LinkRelation relation) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(base.expand());
        paginationResolver.enhance(builder, null, pagination);

        return Link.of(UriTemplate.of(builder.build().toString()), relation);
    }

    private UriTemplate getUriTemplate(Optional<Link> baseLink) {
        return UriTemplate.of(baseLink.map(Link::getHref).orElseGet(SlicedResourcesAssembler::currentRequest));
    }

    private static String currentRequest() {
        return ServletUriComponentsBuilder.fromCurrentRequest().build().toString();
    }
}
