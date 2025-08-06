package com.contentgrid.hateoas.spring.affordances.property;

import java.util.function.UnaryOperator;
import lombok.Getter;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsOptions;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsOptions.AbstractHalFormsOptions;

@Getter
public class PropertyMetadataWithReferenceFields extends CustomPropertyMetadata implements UnaryOperator<HalFormsOptions> {

    private final String promptField;
    private final String valueField;

    public PropertyMetadataWithReferenceFields(PropertyMetadata delegate, String promptField, String valueField) {
        super(delegate);
        this.promptField = promptField;
        this.valueField = valueField;
    }

    @Override
    public HalFormsOptions apply(HalFormsOptions halFormsOptions) {
        if (halFormsOptions instanceof AbstractHalFormsOptions<?> abstractHalFormsOptions) {
            return abstractHalFormsOptions.withPromptField(promptField).withValueField(valueField);
        } else {
            return halFormsOptions;
        }
    }
}
