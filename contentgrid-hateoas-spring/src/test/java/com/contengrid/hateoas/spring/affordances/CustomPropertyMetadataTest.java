package com.contengrid.hateoas.spring.affordances;

import static org.assertj.core.api.Assertions.assertThat;

import com.contentgrid.hateoas.spring.affordances.property.CustomPropertyMetadata;
import com.contentgrid.hateoas.spring.affordances.property.PropertyMetadataWithAllowedValues;
import com.contentgrid.hateoas.spring.affordances.property.PropertyMetadataWithSelectedValue;
import java.util.List;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.mediatype.PropertyUtils;

class CustomPropertyMetadataTest {
    @Data
    static class DataClass {

        String title;

    }

    private static final PropertyMetadata PROPERTY_METADATA;

    static {
        PROPERTY_METADATA = PropertyUtils.getExposedProperties(DataClass.class)
                .stream()
                .findFirst()
                .get();
    }

    @Test
    void customOfStandardPropertyMetadata() {
        var propertyMetadata = CustomPropertyMetadata.custom(PROPERTY_METADATA);

        assertThat(propertyMetadata.getName()).isEqualTo("title");
        assertThat(propertyMetadata.getType().getType()).isEqualTo(String.class);

        assertThat(propertyMetadata.findDelegate(CustomPropertyMetadata.class)).get().isSameAs(propertyMetadata);
        assertThat(propertyMetadata.findDelegate(PropertyMetadataWithAllowedValues.class)).isEmpty();
        assertThat(propertyMetadata.findDelegate(PropertyMetadataWithSelectedValue.class)).isEmpty();
    }

    @Test
    void customOfWithAllowedValues() {
        var withAllowedValues = new PropertyMetadataWithAllowedValues(PROPERTY_METADATA, List.of("a", "b"));
        var propertyMetadata = CustomPropertyMetadata.custom(withAllowedValues);

        assertThat(propertyMetadata.findDelegate(PropertyMetadataWithAllowedValues.class)).hasValueSatisfying(propertyMetadataWithAllowedValues -> {
            assertThat(propertyMetadataWithAllowedValues).isSameAs(withAllowedValues);
        });
        assertThat(propertyMetadata.findDelegate(PropertyMetadataWithSelectedValue.class)).isEmpty();
    }

    @Test
    void customOfWithAllowedValuesAndSelectedValue() {
        var withAllowedValues = new PropertyMetadataWithAllowedValues(PROPERTY_METADATA, List.of("a", "b"));
        var selectedValue = new PropertyMetadataWithSelectedValue(withAllowedValues, "a");
        var propertyMetadata = CustomPropertyMetadata.custom(selectedValue);

        assertThat(propertyMetadata.findDelegate(PropertyMetadataWithAllowedValues.class)).hasValueSatisfying(propertyMetadataWithAllowedValues -> {
            assertThat(propertyMetadataWithAllowedValues).isSameAs(withAllowedValues);
        });
        assertThat(propertyMetadata.findDelegate(PropertyMetadataWithSelectedValue.class)).hasValueSatisfying(propertyMetadataWithSelectedValue -> {
            assertThat(propertyMetadataWithSelectedValue).isSameAs(selectedValue);
        });
    }

    @Test
    void customOfNestedAllowedValues() {
        var withAllowedValues1 = new PropertyMetadataWithAllowedValues(PROPERTY_METADATA, List.of("a", "b"));
        var withAllowedValues2 = new PropertyMetadataWithAllowedValues(withAllowedValues1, List.of("a", "b", "c"));
        var propertyMetadata = CustomPropertyMetadata.custom(withAllowedValues2);

        assertThat(propertyMetadata.findDelegate(PropertyMetadataWithAllowedValues.class)).hasValueSatisfying(propertyMetadataWithAllowedValues -> {
            assertThat(propertyMetadataWithAllowedValues).isSameAs(withAllowedValues2);
        });
        assertThat(propertyMetadata.findDelegate(PropertyMetadataWithSelectedValue.class)).isEmpty();
    }
}