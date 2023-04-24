package com.contentgrid.hateoas.spring.affordances.property;

import com.contentgrid.hateoas.spring.annotations.InternalApi;
import lombok.Getter;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;

public class PropertyMetadataWithSelectedValue extends CustomPropertyMetadata{

    @Getter
    private final Object selectedValue;

    @InternalApi
    public PropertyMetadataWithSelectedValue(PropertyMetadata delegate, Object selectedValue) {
        super(delegate);
        this.selectedValue = selectedValue;
    }
}
