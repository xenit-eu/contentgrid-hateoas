package com.contentgrid.hateoas.spring.affordances.property.modifier;

import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;

@RequiredArgsConstructor
class DroppingPropertyModifier implements PropertyModifier {

    private final String propertyName;

    @Override
    public PropertyMetadata customizeProperty(PropertyMetadata propertyMetadata) {
        return propertyMetadata;
    }

    @Override
    public boolean keepProperty(PropertyMetadata propertyMetadata) {
        return !propertyMetadata.hasName(propertyName);
    }

    @Override
    public Stream<PropertyMetadata> addProperties() {
        return Stream.empty();
    }
}
