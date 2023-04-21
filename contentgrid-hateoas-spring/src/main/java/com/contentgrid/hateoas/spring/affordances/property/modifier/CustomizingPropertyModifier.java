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
    public PropertyMetadata customizeProperty(PropertyMetadata propertyMetadata) {
        if (propertyMetadata.hasName(propertyName)) {
            return customizer.apply(propertyMetadata);
        }
        return propertyMetadata;
    }

    @Override
    public boolean keepProperty(PropertyMetadata propertyMetadata) {
        return true;
    }

    @Override
    public Stream<PropertyMetadata> addProperties() {
        return Stream.empty();
    }
}
