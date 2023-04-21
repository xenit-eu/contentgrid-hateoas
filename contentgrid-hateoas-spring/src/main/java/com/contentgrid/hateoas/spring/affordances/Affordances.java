package com.contentgrid.hateoas.spring.affordances;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Affordance;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mediatype.ConfigurableAffordance;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

/**
 * Allows customizing multiple {@link Affordance}s on a common base affordance
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Affordances implements CustomizableAffordance<Affordances> {
    @NonNull
    private final AffordanceCustomizer baseCustomizer;
    private final boolean includeBase;
    @NonNull
    private final List<Affordance> affordances;

    /**
     * Extract a {@link Link} from the {@link WebMvcLinkBuilder} and creates {@link Affordances} that can be used to
     * configure the affordances.
     *
     * <pre>
     * Affordances updateEmployee = afford(methodOn(EmployeeController.class).updateEmployee(null, id))
     *                              .configure(affordanceBuilder -> affordanceBuilder.withInput(Employee.class))
     *                              .additionally(affordanceCustomizer -> affordanceCustomizer.configure(affordanceBuilder -> affordanceBuilder.withName("patch")))
     *                              ;
     * </pre>
     *
     * This creates an affordance to the updateEmployee method and modifies the input to be an Employee type,
     * then creates an additional affordance with a different name.
     */
    public static Affordances afford(Object invocationValue) {
        return new Affordances(AffordanceCustomizer.afford(invocationValue), true, List.of());
    }

    @Override
    public Affordances configure(UnaryOperator<ConfigurableAffordance> customizer) {
        return new Affordances(baseCustomizer.configure(customizer), includeBase, affordances);
    }

    @Override
    public Affordances configureInput(UnaryOperator<CustomInputPayloadMetadata> customizer) {
        return new Affordances(baseCustomizer.configureInput(customizer), includeBase, affordances);
    }

    /**
     * Creates and configures an additional affordance based on the base affordance as currently configured.
     */
    public Affordances additionally(UnaryOperator<AffordanceCustomizer> configurer) {
        var newAffordance = configurer.apply(baseCustomizer).build();
        return new Affordances(baseCustomizer, includeBase, Stream.concat(affordances.stream(), Stream.of(newAffordance)).toList());
    }

    /**
     * Requests that only additional {@link Affordance}s will be considered
     */
    public Affordances onlyAdditional() {
        return new Affordances(baseCustomizer, false, affordances);
    }

    /**
     * @return stream of all configured affordances
     */
    public Stream<Affordance> stream() {
        if(includeBase) {
            return Stream.concat(
                    Stream.of(baseCustomizer.build()),
                    affordances.stream()
            );
        }
        return affordances.stream();
    }

}
