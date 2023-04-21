package com.contentgrid.hateoas.spring.affordances;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;

@RequiredArgsConstructor
public class CustomPropertyMetadata implements PropertyMetadata {

    @Delegate
    private final PropertyMetadata delegate;

    public static CustomPropertyMetadata custom(PropertyMetadata propertyMetadata) {
        if(propertyMetadata instanceof CustomPropertyMetadata customPropertyMetadata) {
            return customPropertyMetadata;
        }
        return new CustomPropertyMetadata(propertyMetadata);
    }

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
