package com.contentgrid.hateoas.spring.affordances.property;

import com.contentgrid.hateoas.spring.affordances.configuration.OptionsMetadata;
import java.util.Optional;
import java.util.function.Function;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.mediatype.hal.forms.CustomHalFormsConfiguration;

/**
 * Base class for extended customizations of {@link PropertyMetadata}
 *
 * Extended customizations are not part of the Spring HATEOAS {@link PropertyMetadata} API, but are a layer on top of it,
 * and must explicitly be handled in {@link CustomHalFormsConfiguration}
 *
 * @see OptionsMetadata for configuring {@link CustomHalFormsConfiguration#withOptions(Class, String, Function)} based on custom metadata
 */
@RequiredArgsConstructor
public class CustomPropertyMetadata implements PropertyMetadata {

    @Delegate
    @NonNull
    private final PropertyMetadata delegate;

    /**
     * Grabs the existing {@link CustomPropertyMetadata} attached to {@link PropertyMetadata} or creates a new one if there is none attached.
     * @return An enhanced {@link PropertyMetadata}
     */
    public static CustomPropertyMetadata custom(PropertyMetadata propertyMetadata) {
        if(propertyMetadata instanceof CustomPropertyMetadata customPropertyMetadata) {
            return customPropertyMetadata;
        }
        return new CustomPropertyMetadata(propertyMetadata);
    }

    /**
     * Try to locate a customization of a certain type
     * @param clazz The type of the customization to locate
     * @return The concrete customization, or an empty optional if the customization does not exist
     * @param <T> Type of the customization
     */
    public <T extends CustomPropertyMetadata> Optional<T> findDelegate(Class<T> clazz) {
        CustomPropertyMetadata currentDelegate = this;

        while(currentDelegate != null) {

            if(clazz.isInstance(currentDelegate)) {
                return Optional.of((T)currentDelegate);
            }

            if(currentDelegate.delegate instanceof CustomPropertyMetadata customPropertyMetadata) {
                currentDelegate = customPropertyMetadata;
            } else {
                currentDelegate = null;
            }
        }

        return Optional.empty();

    }
}
