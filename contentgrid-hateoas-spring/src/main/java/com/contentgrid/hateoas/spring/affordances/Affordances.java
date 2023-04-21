package com.contentgrid.hateoas.spring.affordances;

import com.contentgrid.hateoas.spring.annotations.PublicApi;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.springframework.hateoas.Affordance;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.ConfigurableAffordance;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpMethod;

/**
 * Allows customizing multiple {@link Affordance}s on a common base affordance
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@PublicApi
@With(value = AccessLevel.PRIVATE)
public class Affordances implements CustomizableAffordance<Affordances> {
    @NonNull
    private final AffordanceCustomizer baseCustomizer;
    @NonNull
    @With(value = AccessLevel.NONE)
    private final Link link;
    private final boolean includeBase;
    private final Affordance defaultAffordance;
    @NonNull
    private final List<Affordance> affordances;

    /**
     * Extract a {@link Link} from the {@link WebMvcLinkBuilder} and creates {@link Affordances} that can be used to
     * configure the affordances.
     *
     * <pre>
     * Affordances updateEmployee = Affordances.on(methodOn(EmployeeController.class).updateEmployee(null, id))
     *                              .configure(affordanceBuilder -> affordanceBuilder.withInput(Employee.class))
     *                              .additionally(affordanceCustomizer -> affordanceCustomizer.configure(affordanceBuilder -> affordanceBuilder.withName("patch")))
     *                              ;
     * </pre>
     *
     * This creates an affordance to the updateEmployee method and modifies the input to be an Employee type,
     * then creates an additional affordance with a different name.
     */
    public static Affordances on(Object invocationValue) {
        var customizer = AffordanceCustomizer.afford(invocationValue);
        return new Affordances(customizer, customizer.build().iterator().next().getLink(), true, null, List.of());
    }

    @Override
    public Affordances configure(UnaryOperator<ConfigurableAffordance> customizer) {
        return withBaseCustomizer(baseCustomizer.configure(customizer));
    }

    @Override
    public Affordances configureInput(UnaryOperator<CustomInputPayloadMetadata> customizer) {
        return withBaseCustomizer(baseCustomizer.configureInput(customizer));
    }

    /**
     * Creates and configures an additional affordance based on the base affordance as currently configured.
     *
     * @see #andAffordance(AffordanceCustomizer) to add a separate, unrelated affordance
     */
    public Affordances additionally(UnaryOperator<AffordanceCustomizer> configurer) {
        return andAffordance(configurer.apply(baseCustomizer));
    }

    /**
     * Adds an additional affordance that is created separately
     *
     * @see #additionally(UnaryOperator) to add an affordance based on the base affordance
     * @see #andAffordance(Affordance) to add a separate affordance not created by {@link AffordanceCustomizer}
     */
    public Affordances andAffordance(AffordanceCustomizer customizer) {
        return andAffordance(customizer.build());
    }

    /**
     * Adds an additional affordance that is created separately
     */
    public Affordances andAffordance(Affordance newAffordance) {
        return withAffordances(Stream.concat(affordances.stream(), Stream.of(newAffordance)).toList());
    }

    /**
     * Adds additional affordances that are created separately
     */
    public Affordances andAffordances(Affordances affordances) {
        return withAffordances(Stream.concat(this.affordances.stream(), affordances.stream()).toList());
    }

    /**
     * Pins down a "default" _template for HAL-FORMS
     */
    public Affordances withDefault() {
        return withDefaultAffordance(createDefaultAffordanceForHalForms());
    }

    /**
     * Requests that only additional {@link Affordance}s will be considered
     */
    public Affordances onlyAdditional() {
        return withIncludeBase(false);
    }

    private Affordance createDefaultAffordanceForHalForms() {
        var baseAffordance = baseCustomizer.build();
        var affordanceModel = baseAffordance.iterator().next();
        // GET method does not result in a HAL-FORMS _template entry,
        // so we need to set a different one to nail down the default affordance
        var defaultMethod = affordanceModel.hasHttpMethod(HttpMethod.GET)?HttpMethod.HEAD:null;
        var defaultAffordance = AffordanceCustomizer.from(baseAffordance, defaultMethod)
                .configure(affordance -> affordance.withName("default").withInputAndOutput(Void.class).withParameters(List.of()))
                .build();
        // We create an affordance here that is only valid for HAL-FORMS, so it does not show up in other representations
        var halFormsModel = defaultAffordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON);
        if(halFormsModel != null) {
            return new Affordance(Map.of(
                    MediaTypes.HAL_FORMS_JSON, halFormsModel
            ));
        } else {
            return new Affordance(Map.of());
        }
    }

    /**
     * @return stream of all configured affordances
     */
    public Stream<Affordance> stream() {
        return Stream.concat(
                Stream.of(
                        defaultAffordance,
                        includeBase?baseCustomizer.build():null
                ).filter(Objects::nonNull),
                affordances.stream()
        );
    }

    /**
     * @return LinkBuilder that contains current link and all configured affordances
     */
    public LinkBuilder toLink() {
        var linkWithAffordances = link.withAffordances(stream().toList());
        return new LinkBuilder() {
            @Override
            public LinkBuilder slash(Object object) {
                throw new UnsupportedOperationException();
            }

            @Override
            public URI toUri() {
                return linkWithAffordances.toUri();
            }

            @Override
            public Link withRel(LinkRelation rel) {
                return linkWithAffordances.withRel(rel);
            }

            @Override
            public Link withSelfRel() {
                return linkWithAffordances.withSelfRel();
            }
        };
    }

}
