package com.contentgrid.hateoas.spring.affordances.property.modifier;

import com.contentgrid.hateoas.spring.affordances.property.BasicPropertyMetadata;
import com.contentgrid.hateoas.spring.affordances.property.PropertyMetadataWithAllowedValues;
import com.contentgrid.hateoas.spring.affordances.property.PropertyMetadataWithItemLimits;
import com.contentgrid.hateoas.spring.affordances.property.PropertyMetadataWithReferenceFields;
import com.contentgrid.hateoas.spring.affordances.property.PropertyMetadataWithRemoteValues;
import com.contentgrid.hateoas.spring.affordances.property.PropertyMetadataWithSelectedValue;
import com.contentgrid.hateoas.spring.annotations.InternalApi;
import com.contentgrid.hateoas.spring.annotations.PublicApi;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.springframework.core.ResolvableType;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.Link;

/**
 * Modifies properties on {@link com.contentgrid.hateoas.spring.affordances.CustomInputPayloadMetadata}
 */
public interface PropertyModifier {

    /**
     * Modifies the {@link PropertyMetadata} stream
     *
     * @param oldProperties The original {@link PropertyMetadata} stream
     * @return The modified {@link PropertyMetadata} stream
     */
    @InternalApi
    Stream<PropertyMetadata> modify(Stream<PropertyMetadata> oldProperties);

    /**
     * Creates a property modifier that applies this and afterwards an other property modifier
     *
     * @param other The additional property modifier to apply
     * @return A composite property modifier that applies this and the other property modifier
     */
    @PublicApi
    default PropertyModifier andThen(PropertyModifier other) {
        return new ComposedPropertyModifier(this, other);
    }

    /**
     * Creates a conditionally enabled or disabled property modifier
     *
     * @param condition Whether to enable or disable this property modifier
     * @return The new property modifier
     */
    @PublicApi
    default PropertyModifier when(boolean condition) {
        return condition ? this : PropertyModifier.none();
    }

    /**
     * Remove a named property
     *
     * @param propertyName The name of the property to remove
     */
    static PropertyModifier drop(String propertyName) {
        return new DroppingPropertyModifier(propertyName);
    }

    /**
     * Modify {@link PropertyMetadata} for a named property
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
     * Add allowed values to a named property
     *
     * @param propertyName The name of the property to modify
     * @param allowedValues A list of option values for the property
     */
    static PropertyModifier addAllowedValues(String propertyName, List<?> allowedValues) {
        return customize(propertyName,
                propertyMetadata -> new PropertyMetadataWithAllowedValues(propertyMetadata, allowedValues));
    }

    /**
     * Add remote values to a named property
     *
     * @param propertyName The name of the property to modify
     * @param href A href pointing to the resource returning option values for the property
     */
    static PropertyModifier addRemoteValues(String propertyName, String href) {
        return addRemoteValues(propertyName, Link.of(href));
    }

    /**
     * Add remote values to a named property
     *
     * @param propertyName The name of the property to modify
     * @param link A link pointing to the resource returning option values for the property
     */
    static PropertyModifier addRemoteValues(String propertyName, Link link) {
        return customize(propertyName,
                propertyMetadata -> new PropertyMetadataWithRemoteValues(propertyMetadata, link));
    }

    /**
     * Add the selected value to a named property
     * @param propertyName The name of the property to modify
     * @param selectedValue The option value(s) that are selected
     */
    static PropertyModifier addSelectedValue(String propertyName, Object selectedValue) {
        return customize(propertyName,
                propertyMetadata -> new PropertyMetadataWithSelectedValue(propertyMetadata, selectedValue));
    }

    /**
     * Add maxItems to the property options. The minItems will automatically be set to 1
     * if propertyMetadata is required, or 0 otherwise.
     *
     * @param propertyName The name of the property to modify
     * @param maxItems The maximum number of items to be selected
     */
    static PropertyModifier addMaxItems(String propertyName, Long maxItems) {
        return addItemLimits(propertyName, null, maxItems);
    }

    /**
     * Add minItems and maxItems to the property options
     *
     * @param propertyName The name of the property to modify
     * @param minItems The minimum number of items to be selected
     * @param maxItems The maximum number of items to be selected
     */
    static PropertyModifier addItemLimits(String propertyName, Long minItems, Long maxItems) {
        return customize(propertyName,
                propertyMetadata -> new PropertyMetadataWithItemLimits(propertyMetadata, minItems, maxItems));
    }

    /**
     * Add promptField and valueField to the property options
     *
     * @param propertyName The name of the property to modify
     * @param promptField The name of the field that is used as prompt
     * @param valueField The name of the field that is used as value
     */
    static PropertyModifier addReferenceFields(String propertyName, String promptField, String valueField) {
        return customize(propertyName,
                propertyMetadata -> new PropertyMetadataWithReferenceFields(propertyMetadata, promptField, valueField));
    }

    /**
     * Add a named property of a certain type
     * @param propertyName The name of the property to add
     * @param type The type of the property to add
     *
     * @see #add(String, ResolvableType) for adding properties based on a generic type
     * @see #add(String, Class, Function) for immediately customizing the {@link PropertyMetadata} of the added property
     */
    static PropertyModifier add(String propertyName, Class<?> type) {
        return add(propertyName, ResolvableType.forClass(type));
    }

    /**
     * Add a named property of a certain type
     * @param propertyName The name of the property to add
     * @param type The type of the property to add
     *
     * @see #add(String, Class) for adding properties based on {@link Class}
     * @see #add(String, ResolvableType, Function) for immediately customizing the {@link PropertyMetadata} of the added property
     */
    static PropertyModifier add(String propertyName, ResolvableType type) {
        return add(propertyName, type, Function.identity());
    }

    /**
     * Add a named property of a certain type and configure {@link PropertyMetadata}
     * @param propertyName The name of the property to add
     * @param type The type of the property to add
     * @param customizer Sets up property metadata for the new property
     */
    static PropertyModifier add(String propertyName, Class<?> type,
            Function<BasicPropertyMetadata, ? extends PropertyMetadata> customizer) {
        return add(propertyName, ResolvableType.forClass(type), customizer);
    }

    /**
     * Add a named property of a certain type and configure {@link PropertyMetadata}
     * @param propertyName The name of the property to add
     * @param type The type of the property to add
     * @param customizer Sets up property metadata for the new property
     */
    static PropertyModifier add(String propertyName, ResolvableType type,
            Function<BasicPropertyMetadata, ? extends PropertyMetadata> customizer) {
        return new InsertingPropertyModifier(customizer.apply(new BasicPropertyMetadata(propertyName, type)));
    }

    /**
     * Does nothing
     */
    static PropertyModifier none() {
        return new NonePropertyModifier();
    }

}
