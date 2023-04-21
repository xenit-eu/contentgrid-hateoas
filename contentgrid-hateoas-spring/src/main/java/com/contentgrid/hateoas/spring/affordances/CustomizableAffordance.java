package com.contentgrid.hateoas.spring.affordances;

import com.contentgrid.hateoas.spring.affordances.CustomInputPayloadMetadata.PropertyModifier;
import java.util.function.UnaryOperator;
import org.springframework.hateoas.mediatype.ConfigurableAffordance;

public interface CustomizableAffordance<T extends CustomizableAffordance<T>> {

    /**
     * Customize an {@link ConfigurableAffordance}
     *
     * @param customizer Applies changes and returns the customized variant
     */
    T configure(UnaryOperator<ConfigurableAffordance> customizer);

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
    T configureInput(UnaryOperator<CustomInputPayloadMetadata> customizer);

    /**
     * Customizes the affordance input metadata with {@link PropertyModifier}
     *
     * <pre>
     * Affordance updateEmployee = afford(methodOn(EmployeeController.class).updateEmployee(null, id))
     *                              .configureInput(PropertyModifier.drop("badgeNumber")
     *                              .build();
     * </pre>
     *
     * @param propertyModifier The property modifier to apply on the affordance input
     */
    default T configureInput(PropertyModifier propertyModifier) {
        return configureInput(inputPayloadMetadata -> inputPayloadMetadata.with(propertyModifier));
    }
}
