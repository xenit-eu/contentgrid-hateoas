package com.contentgrid.hateoas.spring.affordances;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import com.contentgrid.hateoas.spring.annotations.PublicApi;
import java.util.Objects;
import java.util.function.UnaryOperator;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.springframework.hateoas.Affordance;
import org.springframework.hateoas.mediatype.Affordances;
import org.springframework.hateoas.AffordanceModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mediatype.ConfigurableAffordance;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

/**
 * Allows customizing an {@link Affordance} to change some of its properties
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@PublicApi
public class AffordanceCustomizer implements CustomizableAffordance<AffordanceCustomizer> {

    @NonNull
    private final Affordance primaryAffordance;

    @With(value = AccessLevel.PRIVATE)
    @NonNull
    private final ConfigurableAffordance configurableAffordance;

    /**
     * Extract a {@link Link} from the {@link WebMvcLinkBuilder} and creates an {@link AffordanceCustomizer} to modify
     * the affordance.
     *
     * <pre>
     * Affordance updateEmployee = afford(methodOn(EmployeeController.class).updateEmployee(null, id))
     *                              .configure(affordanceBuilder -> affordanceBuilder.withInput(Employee.class))
     *                              .build();
     * </pre>
     *
     * This creates an affordance to the updateEmployee method and modifies the input to be an Employee type.
     */
    public static AffordanceCustomizer afford(Object invocationValue) {
        var link = linkTo(invocationValue);

        var primaryAffordance = Objects.requireNonNull(link.getAffordances().get(0), "primaryAffordance");

        AffordanceModel affordanceModel = Objects.requireNonNull(primaryAffordance.iterator().next(),
                "affordanceModel");

        var affordances = Affordances.of(link.withSelfRel());
        var newAffordance = affordances.afford(affordanceModel.getHttpMethod())
                .withName(affordanceModel.getName())
                .withInput(affordanceModel.getInput())
                .withOutput(affordanceModel.getOutput())
                .withParameters(affordanceModel.getQueryMethodParameters());

        return new AffordanceCustomizer(primaryAffordance, newAffordance);
    }

    /**
     * Customize an {@link ConfigurableAffordance}
     *
     * @param customizer Applies changes and returns the customized variant
     */
    @Override
    public AffordanceCustomizer configure(UnaryOperator<ConfigurableAffordance> customizer) {
        return withConfigurableAffordance(customizer.apply(configurableAffordance));
    }

    /**
     * Customizes the affordance input metadata with {@link CustomInputPayloadMetadata}
     *
     * <pre>
     * Affordance updateEmployee = afford(methodOn(EmployeeController.class).updateEmployee(null, id))
     *                              .configureInput(customInputPayloadMetadata -> customInputPayloadMetadata.with(PropertyModifier.drop("badgeNumber"))
     *                              .build();
     * </pre>
     *
     * @param customizer Applies changes to the affordance input and returns the customized input
     */
    @Override
    public AffordanceCustomizer configureInput(UnaryOperator<CustomInputPayloadMetadata> customizer) {
        var builtAffordance = Objects.requireNonNull(build().iterator().next(), "builtAffordance");
        var inputPayloadMetadata = CustomInputPayloadMetadata.from(builtAffordance.getInput());
        return configure(affordance -> affordance.withInput(customizer.apply(inputPayloadMetadata)));
    }

    /**
     * Builds the customized {@link Affordance}
     *
     * @return The customized affordance
     */
    public Affordance build() {
        return configurableAffordance.build()
                .stream()
                .filter(affordance -> affordance != primaryAffordance)
                .reduce((a, b) -> {
                    throw new IllegalStateException("Found more than one newly created affordance");
                })
                .orElseThrow(() -> new IllegalStateException("Could not find newly created affordance"));
    }
}
