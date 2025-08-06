package com.contentgrid.hateoas.spring.affordances.configuration;

import com.contentgrid.hateoas.spring.affordances.property.CustomPropertyMetadata;
import com.contentgrid.hateoas.spring.affordances.property.PropertyMetadataWithAllowedValues;
import com.contentgrid.hateoas.spring.affordances.property.PropertyMetadataWithItemLimits;
import com.contentgrid.hateoas.spring.affordances.property.PropertyMetadataWithReferenceFields;
import com.contentgrid.hateoas.spring.affordances.property.PropertyMetadataWithRemoteValues;
import com.contentgrid.hateoas.spring.affordances.property.PropertyMetadataWithSelectedValue;
import com.contentgrid.hateoas.spring.annotations.PublicApi;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsOptions;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsOptions.AbstractHalFormsOptions;

/**
 * Sets up options metadata in {@link org.springframework.hateoas.mediatype.hal.forms.HalFormsConfiguration#withOptions(Class, String, Function)}
 *
 * <pre>
 *     HalFormsConfiguration halFormsConfiguration;
 *     halFormsConfiguration
 *          .withOptions(Employee.class, "department", new OptionsMetadata(DepartmentEnum.values()))
 *          .withOptions(Employee.class, "type", new OptionsMetadata());
 * </pre>
 */
@RequiredArgsConstructor
@PublicApi
public class OptionsMetadata implements Function<PropertyMetadata, HalFormsOptions> {
    @NonNull
    private final Collection<?> defaultValues;

    public OptionsMetadata(Object... defaultValues) {
        this(Arrays.asList(defaultValues));
    }

    @Override
    public HalFormsOptions apply(PropertyMetadata metadata) {
        var customMetadata = CustomPropertyMetadata.custom(metadata);
        var atomicResult = new AtomicReference<AbstractHalFormsOptions<?>>(customMetadata
                .findDelegate(PropertyMetadataWithAllowedValues .class)
                .map(PropertyMetadataWithAllowedValues::getAllowedValues)
                .map(HalFormsOptions::inline)
                .map(AbstractHalFormsOptions.class::cast)
                .orElseGet(() -> customMetadata.findDelegate(PropertyMetadataWithRemoteValues.class)
                        .map(PropertyMetadataWithRemoteValues::getLink)
                        .map(HalFormsOptions::remote)
                        .map(AbstractHalFormsOptions.class::cast)
                        .orElse(HalFormsOptions.inline(defaultValues))));

        customMetadata.findDelegate(PropertyMetadataWithSelectedValue.class)
                .ifPresent(pm -> atomicResult.updateAndGet(result ->
                        result.withSelectedValue(pm.getSelectedValue())));
        customMetadata.findDelegate(PropertyMetadataWithItemLimits.class)
                .ifPresent(pm -> atomicResult.updateAndGet(result ->
                        result.withMinItems(pm.getMinItems()).withMaxItems(pm.getMaxItems())));
        customMetadata.findDelegate(PropertyMetadataWithReferenceFields.class)
                .ifPresent(pm -> atomicResult.updateAndGet(result ->
                        result.withPromptField(pm.getPromptField()).withValueField(pm.getValueField())));

        return atomicResult.get();
    }

}
