package com.contentgrid.hateoas.spring.affordances.property;

import com.contentgrid.hateoas.spring.annotations.InternalApi;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;

public class PropertyMetadataWithAllowedValues extends CustomPropertyMetadata {

    @Getter
    @NonNull
    private final List<?> allowedValues;

    @InternalApi
    public PropertyMetadataWithAllowedValues(PropertyMetadata delegate, List<?> allowedValues) {
        super(delegate);
        this.allowedValues = allowedValues;
    }
}
