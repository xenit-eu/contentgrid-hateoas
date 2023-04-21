package com.contentgrid.hateoas.spring.affordances.property.modifier;

import com.contentgrid.hateoas.spring.affordances.property.BasicPropertyMetadata;
import com.contentgrid.hateoas.spring.affordances.property.PropertyMetadataWithAllowedValues;
import com.contentgrid.hateoas.spring.affordances.property.PropertyMetadataWithSelectedValue;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.springframework.core.ResolvableType;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;

public interface PropertyModifier {

    PropertyMetadata customizeProperty(PropertyMetadata metadata);

    boolean keepProperty(PropertyMetadata metadata);

    Stream<PropertyMetadata> addProperties();

    default PropertyModifier when(boolean condition) {
        return condition ? this : PropertyModifier.none();
    }

    /**
     * Removes a named property
     *
     * @param propertyName The name of the property to remove
     */
    static PropertyModifier drop(String propertyName) {
        return new DroppingPropertyModifier(propertyName);
    }

    /**
     * Modifies {@link PropertyMetadata} for a named property
     *
     * @param propertyName The name of the property to modify
     * @param customizer Returns a new {@link PropertyMetadata} that incorporates the requested changes
     * @see #addAllowedValues(String, List)
     */
    static PropertyModifier customize(String propertyName,
            UnaryOperator<PropertyMetadata> customizer) {
        return new CustomizingPropertyModifier(propertyName, customizer);
    }

    /**
     * Adds allowed values to a named property
     *
     * @param propertyName The name of the property to modify
     * @param allowedValues A list of option values for the property
     */
    static PropertyModifier addAllowedValues(String propertyName, List<?> allowedValues) {
        return customize(propertyName,
                propertyMetadata -> new PropertyMetadataWithAllowedValues(propertyMetadata, allowedValues));
    }

    static PropertyModifier addSelectedValue(String propertyName, Object selectedValue) {
        return customize(propertyName,
                propertyMetadata -> new PropertyMetadataWithSelectedValue(propertyMetadata, selectedValue));
    }

    static PropertyModifier add(String propertyName, Class<?> type) {
        return add(propertyName, ResolvableType.forClass(type));
    }

    static PropertyModifier add(String propertyName, ResolvableType type) {
        return add(propertyName, type, Function.identity());
    }

    static PropertyModifier add(String propertyName, Class<?> type,
            Function<BasicPropertyMetadata, ? extends PropertyMetadata> customizer) {
        return add(propertyName, ResolvableType.forClass(type), customizer);
    }

    static PropertyModifier add(String propertyName, ResolvableType type,
            Function<BasicPropertyMetadata, ? extends PropertyMetadata> customizer) {
        return new InsertingPropertyModifier(customizer.apply(new BasicPropertyMetadata(propertyName, type)));
    }

    /**
     *
     */
    static PropertyModifier none() {
        return new NonePropertyModifier();
    }

}
