package com.contentgrid.hateoas.spring.affordances;

import lombok.Getter;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;

public class PropertyMetadataWithSelectedValue extends CustomPropertyMetadata{

    @Getter
    private final Object selectedValue;

    public PropertyMetadataWithSelectedValue(PropertyMetadata delegate, Object selectedValue) {
        super(delegate);
        this.selectedValue = selectedValue;
    }
}
