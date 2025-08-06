package com.contentgrid.hateoas.spring.affordances.property;

import com.contentgrid.hateoas.spring.annotations.InternalApi;
import java.util.List;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsOptions;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsOptions.AbstractHalFormsOptions;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsOptions.Inline;

public class PropertyMetadataWithAllowedValues extends CustomPropertyMetadata implements Supplier<HalFormsOptions> {

    @Getter
    @NonNull
    private final List<?> allowedValues;

    @InternalApi
    public PropertyMetadataWithAllowedValues(PropertyMetadata delegate, List<?> allowedValues) {
        super(delegate);
        this.allowedValues = allowedValues;
    }

    @Override
    public AbstractHalFormsOptions<Inline> get() {
        return HalFormsOptions.inline(allowedValues);
    }
}
