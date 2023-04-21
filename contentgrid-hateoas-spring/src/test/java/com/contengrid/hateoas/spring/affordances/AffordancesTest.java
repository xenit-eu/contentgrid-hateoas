package com.contengrid.hateoas.spring.affordances;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.contengrid.hateoas.spring.affordances.AffordanceCustomizerTest.TestRestController;
import com.contentgrid.hateoas.spring.affordances.Affordances;
import com.contentgrid.hateoas.spring.affordances.CustomInputPayloadMetadata.PropertyModifier;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.AffordanceModel;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpMethod;

@WebMvcTest(TestRestController.class)
class AffordancesTest {
    @Test
    void defaultAfford() {
        var standardAffordances = Affordances.afford(methodOn(TestRestController.class).patch("abc", null));

        assertThat(standardAffordances.stream()).satisfiesExactly(affordance -> {
            assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                assertThat(affordanceModel.getName()).isEqualTo("patch");
                assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.PATCH);
                assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/abc");
            });
        });
    }

    @Test
    void additionallAfford() {
        var standardAffordances = Affordances.afford(methodOn(TestRestController.class).patch("abc", null))
                .configure(configurableAffordance -> configurableAffordance.withName("someName"))
                .additionally(additionalCustomizer -> additionalCustomizer.configureInput(PropertyModifier.drop("name")));

        assertThat(standardAffordances.stream()).satisfiesExactly(
                affordance -> {
                    assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                        assertThat(affordanceModel.getName()).isEqualTo("someName");
                        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.PATCH);
                        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/abc");
                        assertThat(affordanceModel.getInput()).satisfies(inputPayloadMetadata -> {
                            assertThat(inputPayloadMetadata.stream())
                                    .map(PropertyMetadata::getName)
                                    .contains("name");
                        });
                    });
                },
                affordance -> {
                    assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                        assertThat(affordanceModel.getName()).isEqualTo("someName");
                        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.PATCH);
                        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/abc");
                        assertThat(affordanceModel.getInput()).satisfies(inputPayloadMetadata -> {
                            assertThat(inputPayloadMetadata.stream())
                                    .map(PropertyMetadata::getName)
                                    .doesNotContain("name");
                        });
                    });
                }
        );

    }

}