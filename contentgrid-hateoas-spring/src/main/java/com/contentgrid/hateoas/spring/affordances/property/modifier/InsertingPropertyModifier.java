package com.contentgrid.hateoas.spring.affordances.property.modifier;

import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;

@RequiredArgsConstructor
class InsertingPropertyModifier implements PropertyModifier {

    private final PropertyMetadata metadata;

    @Override
    public PropertyMetadata customizeProperty(PropertyMetadata propertyMetadata) {
        return propertyMetadata;
    }

    @Override
    public boolean keepProperty(PropertyMetadata propertyMetadata) {
        return true;
    }

    @Override
    public Stream<PropertyMetadata> addProperties() {
        return Stream.of(metadata);
    }
}
