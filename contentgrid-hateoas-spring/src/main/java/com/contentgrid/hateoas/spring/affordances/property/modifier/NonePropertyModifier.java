package com.contentgrid.hateoas.spring.affordances.property.modifier;

import java.util.stream.Stream;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;

class NonePropertyModifier implements PropertyModifier {

    @Override
    public Stream<PropertyMetadata> modify(Stream<PropertyMetadata> oldProperties) {
        return oldProperties;
    }
}
