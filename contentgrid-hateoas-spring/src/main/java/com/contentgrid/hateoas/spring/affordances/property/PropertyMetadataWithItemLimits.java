package com.contentgrid.hateoas.spring.affordances.property;

import java.util.function.UnaryOperator;
import lombok.Getter;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsOptions;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsOptions.AbstractHalFormsOptions;

@Getter
public class PropertyMetadataWithItemLimits extends CustomPropertyMetadata implements UnaryOperator<HalFormsOptions> {

    private final long minItems;
    private final Long maxItems;

    public PropertyMetadataWithItemLimits(PropertyMetadata delegate, Long minItems, Long maxItems) {
        super(delegate);
        this.minItems = minItems == null ? (delegate.isRequired() ? 1 : 0) : minItems;
        this.maxItems = maxItems;
    }

    @Override
    public HalFormsOptions apply(HalFormsOptions halFormsOptions) {
        if (halFormsOptions instanceof AbstractHalFormsOptions<?> abstractHalFormsOptions) {
            return abstractHalFormsOptions.withMinItems(minItems).withMaxItems(maxItems);
        } else {
            return halFormsOptions;
        }
    }
}
