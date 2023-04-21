package com.contentgrid.hateoas.spring.affordances.property.modifier;

import java.util.function.Function;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;

@RequiredArgsConstructor
class CustomizingPropertyModifier implements PropertyModifier {

    private final String propertyName;
    private final Function<PropertyMetadata, PropertyMetadata> customizer;

    @Override
    public Stream<PropertyMetadata> modify(Stream<PropertyMetadata> oldProperties) {
        return oldProperties.map(this::customizeProperty);
    }

    private PropertyMetadata customizeProperty(PropertyMetadata propertyMetadata) {
        if (propertyMetadata.hasName(propertyName)) {
            return customizer.apply(propertyMetadata);
        }
        return propertyMetadata;
    }

}
