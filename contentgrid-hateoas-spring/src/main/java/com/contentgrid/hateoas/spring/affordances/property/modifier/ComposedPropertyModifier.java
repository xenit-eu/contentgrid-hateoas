package com.contentgrid.hateoas.spring.affordances.property.modifier;

import java.util.stream.Stream;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;

@RequiredArgsConstructor
class ComposedPropertyModifier implements PropertyModifier {
    @NonNull
    private final PropertyModifier first;
    @NonNull
    private final PropertyModifier second;

    @Override
    public Stream<PropertyMetadata> modify(Stream<PropertyMetadata> oldProperties) {
        return second.modify(first.modify(oldProperties));
    }
}
