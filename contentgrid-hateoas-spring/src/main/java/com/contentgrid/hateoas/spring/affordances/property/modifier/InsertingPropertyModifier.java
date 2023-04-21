package com.contentgrid.hateoas.spring.affordances.property.modifier;

import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;

@RequiredArgsConstructor
class InsertingPropertyModifier implements PropertyModifier {

    private final PropertyMetadata metadata;

    @Override
    public Stream<PropertyMetadata> modify(Stream<PropertyMetadata> oldProperties) {
        return Stream.concat(oldProperties, Stream.of(metadata));
    }
}
