package com.contentgrid.hateoas.spring.affordances.property.modifier;

import java.util.stream.Stream;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;

class NonePropertyModifier implements PropertyModifier {

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
        return Stream.empty();
    }
}
