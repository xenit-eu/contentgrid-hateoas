package com.contentgrid.hateoas.spring.hal.forms;

import com.contentgrid.hateoas.spring.affordances.property.CustomPropertyMetadata;
import com.contentgrid.hateoas.spring.affordances.property.PropertyMetadataWithAllowedValues;
import com.contentgrid.hateoas.spring.affordances.property.PropertyMetadataWithSelectedValue;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsOptions;

/**
 * Sets up options metadata in {@link org.springframework.hateoas.mediatype.hal.forms.HalFormsConfiguration#withOptions(Class, String, Function)}
 *
 *
 */
@RequiredArgsConstructor
public class OptionsMetadata implements Function<PropertyMetadata, HalFormsOptions> {
    @NonNull
    private final Collection<?> defaultValues;

    public OptionsMetadata(Object... defaultValues) {
        this(Arrays.asList(defaultValues));
    }

    @Override
    public HalFormsOptions apply(PropertyMetadata metadata) {
        var customMetadata = CustomPropertyMetadata.custom(metadata);
        return customMetadata
                .findDelegate(PropertyMetadataWithAllowedValues .class)
                .map(PropertyMetadataWithAllowedValues::getAllowedValues)
                .map(HalFormsOptions::inline)
                .orElse(HalFormsOptions.inline(defaultValues))
                .withSelectedValue(
                        customMetadata
                                .findDelegate(PropertyMetadataWithSelectedValue.class)
                                .map(PropertyMetadataWithSelectedValue::getSelectedValue)
                                .orElse(null)
                );
    }

}
