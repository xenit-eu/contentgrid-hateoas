package com.contentgrid.hateoas.spring.affordances.property.modifier;

import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;

@RequiredArgsConstructor
class DroppingPropertyModifier implements PropertyModifier {

    private final String propertyName;

    @Override
    public Stream<PropertyMetadata> modify(Stream<PropertyMetadata> oldProperties) {
        return oldProperties.filter(this::keepProperty);
    }
    private boolean keepProperty(PropertyMetadata propertyMetadata) {
        return !propertyMetadata.hasName(propertyName);
    }
}
