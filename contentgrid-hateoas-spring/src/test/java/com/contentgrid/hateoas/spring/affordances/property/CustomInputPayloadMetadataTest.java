package com.contentgrid.hateoas.spring.affordances.property;

import static org.assertj.core.api.Assertions.assertThat;

import com.contentgrid.hateoas.spring.affordances.CustomInputPayloadMetadata;
import com.contentgrid.hateoas.spring.affordances.property.modifier.PropertyModifier;
import java.util.List;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.html.HtmlInputType;

class CustomInputPayloadMetadataTest {

    @Data
    static class DataClass {

        String title;

        boolean optA;
        boolean optB;
    }

    @Test
    void createFromClass() {
        var payloadMetadata = CustomInputPayloadMetadata.from(DataClass.class);
        var withJsonMediatype = payloadMetadata.withMediaTypes(List.of(MediaTypes.HAL_FORMS_JSON));

        assertThat(payloadMetadata.getType()).isEqualTo(DataClass.class);
        assertThat(payloadMetadata.stream())
                .satisfiesExactlyInAnyOrder(
                        title -> {
                            assertThat(title.getName()).isEqualTo("title");
                            assertThat(title.getInputType()).isEqualTo(HtmlInputType.TEXT_VALUE);
                        },
                        optA -> {
                            assertThat(optA.getName()).isEqualTo("optA");
                            assertThat(optA.getInputType()).isEqualTo(HtmlInputType.CHECKBOX_VALUE);
                        },
                        optB -> {
                            assertThat(optB.getName()).isEqualTo("optB");
                            assertThat(optB.getInputType()).isEqualTo(HtmlInputType.CHECKBOX_VALUE);
                        }
                );

        assertThat(withJsonMediatype.getMediaTypes()).containsExactly(MediaTypes.HAL_FORMS_JSON);
        assertThat(withJsonMediatype.stream())
                .satisfiesExactlyInAnyOrder(
                        title -> {
                            assertThat(title.getName()).isEqualTo("title");
                            assertThat(title.getInputType()).isEqualTo(HtmlInputType.TEXT_VALUE);
                        },
                        optA -> {
                            assertThat(optA.getName()).isEqualTo("optA");
                            assertThat(optA.getInputType()).isEqualTo(HtmlInputType.CHECKBOX_VALUE);
                        },
                        optB -> {
                            assertThat(optB.getName()).isEqualTo("optB");
                            assertThat(optB.getInputType()).isEqualTo(HtmlInputType.CHECKBOX_VALUE);
                        }
                );
    }

    @Test
    void dropProperty() {
        var payloadMetadata = CustomInputPayloadMetadata.from(DataClass.class)
                .with(PropertyModifier.drop("optA"));
        var withJsonMediatype = payloadMetadata.withMediaTypes(List.of(MediaTypes.HAL_FORMS_JSON));

        assertThat(payloadMetadata.stream())
                .map(PropertyMetadata::getName)
                .containsExactlyInAnyOrder("title", "optB");

        assertThat(withJsonMediatype.getMediaTypes()).containsExactly(MediaTypes.HAL_FORMS_JSON);
        assertThat(withJsonMediatype.stream())
                .map(PropertyMetadata::getName)
                .containsExactlyInAnyOrder("title", "optB");
    }

    @Test
    void dropProperties() {
        var payloadMetadata = CustomInputPayloadMetadata.from(DataClass.class)
                .with(PropertyModifier.drop("optA"))
                .with(PropertyModifier.drop("title"));

        assertThat(payloadMetadata.stream())
                .map(PropertyMetadata::getName)
                .containsExactlyInAnyOrder("optB");
    }

    @Test
    void propertyModifierWhen() {
        var payloadMetadata = CustomInputPayloadMetadata.from(DataClass.class)
                .with(PropertyModifier.drop("optA").when(true))
                .with(PropertyModifier.drop("title").when(false));

        assertThat(payloadMetadata.stream())
                .map(PropertyMetadata::getName)
                .containsExactlyInAnyOrder("title", "optB");
    }

    @Test
    void propertyAllowedValues() {
        var payloadMetadata = CustomInputPayloadMetadata.from(DataClass.class)
                .with(PropertyModifier.addAllowedValues("title", List.of("title1", "title2")));

        assertThat(payloadMetadata.stream())
                .filteredOn(propertyMetadata -> propertyMetadata.getName().equals("title"))
                .satisfiesExactly(propertyMetadata -> {
                    assertThat(propertyMetadata)
                            .isInstanceOfSatisfying(PropertyMetadataWithAllowedValues.class, allowedValues -> {
                                assertThat(allowedValues.getAllowedValues()).isEqualTo(List.of("title1", "title2"));
                            });
                });
    }

    @Test
    void propertySelectedValue() {
        var payloadMetadata = CustomInputPayloadMetadata.from(DataClass.class)
                .with(PropertyModifier.addSelectedValue("title", "title1"));

        assertThat(payloadMetadata.stream())
                .filteredOn(propertyMetadata -> propertyMetadata.getName().equals("title"))
                .satisfiesExactly(propertyMetadata -> {
                    assertThat(propertyMetadata)
                            .isInstanceOfSatisfying(PropertyMetadataWithSelectedValue.class, allowedValues -> {
                                assertThat(allowedValues.getSelectedValue()).isEqualTo("title1");
                            });
                });
    }

    @Test
    void addProperty() {
        var payloadMetadata = CustomInputPayloadMetadata.from(DataClass.class)
                .with(PropertyModifier.add("subTitle", String.class));

        assertThat(payloadMetadata.stream())
                .map(PropertyMetadata::getName)
                .containsExactlyInAnyOrder("title", "subTitle", "optA", "optB");

        assertThat(payloadMetadata.stream())
                .filteredOn(propertyMetadata -> propertyMetadata.getName().equals("subTitle"))
                .satisfiesExactly(propertyMetadata -> {
                    assertThat(propertyMetadata.getType().getType()).isEqualTo(String.class);
                    assertThat(propertyMetadata.isRequired()).isFalse();
                    // Default of a synthetic property is readonly
                    assertThat(propertyMetadata.isReadOnly()).isTrue();
                });
    }

    @Test
    void addPropertyAndCustomize() {
        var payloadMetadata = CustomInputPayloadMetadata.from(DataClass.class)
                .with(PropertyModifier.add("subTitle", String.class, propertyMetadata -> propertyMetadata.withRequired(true).withReadOnly(false)))
                .with(PropertyModifier.addSelectedValue("subTitle", "abc"));

        assertThat(payloadMetadata.stream())
                .filteredOn(propertyMetadata -> propertyMetadata.getName().equals("subTitle"))
                .satisfiesExactly(propertyMetadata -> {
                    assertThat(propertyMetadata.getType().getType()).isEqualTo(String.class);
                    assertThat(propertyMetadata.isRequired()).isTrue();
                    assertThat(propertyMetadata.isReadOnly()).isFalse();

                    assertThat(CustomPropertyMetadata.custom(propertyMetadata).findDelegate(PropertyMetadataWithSelectedValue.class))
                            .hasValueSatisfying(propertyMetadataWithSelectedValue -> {
                                assertThat(propertyMetadataWithSelectedValue.getSelectedValue()).isEqualTo("abc");
                            });
                });
    }

}