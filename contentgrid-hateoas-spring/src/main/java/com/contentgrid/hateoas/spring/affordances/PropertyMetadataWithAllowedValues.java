package com.contentgrid.hateoas.spring.affordances;

import java.util.List;
import lombok.Getter;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;

public class PropertyMetadataWithAllowedValues extends CustomPropertyMetadata {

    @Getter
    private final List<?> allowedValues;

    public PropertyMetadataWithAllowedValues(PropertyMetadata delegate, List<?> allowedValues) {
        super(delegate);
        this.allowedValues = allowedValues;
    }
}
