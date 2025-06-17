package com.contentgrid.hateoas.spring.affordances;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.contentgrid.hateoas.spring.affordances.AffordanceCustomizerTest.TestRestController;
import com.contentgrid.hateoas.spring.affordances.property.modifier.PropertyModifier;
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
        var standardAffordances = Affordances.of(methodOn(TestRestController.class).patchTestResource("abc", null));

        assertThat(standardAffordances.stream()).satisfiesExactly(affordance -> {
            assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                assertThat(affordanceModel.getName()).isEqualTo("patchTestResource");
                assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.PATCH);
                assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/abc");
            });
        });
    }

    @Test
    void additionalAfford() {
        var standardAffordances = Affordances.of(methodOn(TestRestController.class).patchTestResource("abc", null))
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

    @Test
    void addAffordance() {
        var affordances = Affordances.of(methodOn(TestRestController.class).getTestResource("abc"))
                .andAffordance(AffordanceCustomizer.afford(methodOn(TestRestController.class).patchTestResource("def", null)));

        assertThat(affordances.stream()).satisfiesExactly(
                affordance -> {
                    assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                        assertThat(affordanceModel.getName()).isEqualTo("getTestResource");
                        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.GET);
                        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/abc");
                    });
                },
                affordance -> {
                    assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                        assertThat(affordanceModel.getName()).isEqualTo("patchTestResource");
                        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.PATCH);
                        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/def");
                    });
                }
        );
    }

    @Test
    void withDefault_get_method() {
        var affordances = Affordances.of(methodOn(TestRestController.class).getTestResource("abc"))
                .andAffordance(AffordanceCustomizer.afford(methodOn(TestRestController.class).patchTestResource("xyz", null)))
                .withDefault();

        assertThat(affordances.stream()).satisfiesExactly(
                affordance -> {
                    assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                        assertThat(affordanceModel.getName()).isEqualTo("default");
                        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.HEAD);
                        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/abc");
                    });
                },
                affordance -> {
                    assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                        assertThat(affordanceModel.getName()).isEqualTo("getTestResource");
                        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.GET);
                        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/abc");
                    });
                },
                affordance -> {
                    assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                        assertThat(affordanceModel.getName()).isEqualTo("patchTestResource");
                        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.PATCH);
                        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/xyz");
                    });
                }
        );
    }

    @Test
    void withDefault_patch_method() {
        var affordances = Affordances.of(methodOn(TestRestController.class).patchTestResource("abc", null))
                .andAffordance(AffordanceCustomizer.afford(methodOn(TestRestController.class).patchTestResource("xyz", null)))
                .withDefault();

        assertThat(affordances.stream()).satisfiesExactly(
                affordance -> {
                    assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                        assertThat(affordanceModel.getName()).isEqualTo("default");
                        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.PATCH);
                        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/abc");
                    });
                },
                affordance -> {
                    assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                        assertThat(affordanceModel.getName()).isEqualTo("patchTestResource");
                        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.PATCH);
                        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/abc");
                    });
                },
                affordance -> {
                    assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                        assertThat(affordanceModel.getName()).isEqualTo("patchTestResource");
                        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.PATCH);
                        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/xyz");
                    });
                }
        );
    }

    @Test
    void withDefault_onlyAdditional() {
        var affordances = Affordances.of(methodOn(TestRestController.class).getTestResource("abc"))
                .withDefault()
                .andAffordance(AffordanceCustomizer.afford(methodOn(TestRestController.class).patchTestResource("xyz", null)))
                .onlyAdditional();

        assertThat(affordances.stream()).satisfiesExactly(
                affordance -> {
                    assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                        assertThat(affordanceModel.getName()).isEqualTo("default");
                        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.HEAD);
                        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/abc");
                    });
                },
                affordance -> {
                    assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                        assertThat(affordanceModel.getName()).isEqualTo("patchTestResource");
                        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.PATCH);
                        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/xyz");
                    });
                }
        );
    }

    @Test
    void addAffordances() {
        var affordances = Affordances.of(methodOn(TestRestController.class).getTestResource("abc"))
                .andAffordances(
                        Affordances.of(methodOn(TestRestController.class).patchTestResource("xyz", null))
                                .additionally(customizer -> customizer.configure(affordance -> affordance.withName("patch")))
                );
        assertThat(affordances.stream()).satisfiesExactly(
                affordance -> {
                    assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                        assertThat(affordanceModel.getName()).isEqualTo("getTestResource");
                        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.GET);
                        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/abc");
                    });
                },
                affordance -> {
                    assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                        assertThat(affordanceModel.getName()).isEqualTo("patchTestResource");
                        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.PATCH);
                        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/xyz");
                    });
                },
                affordance -> {
                    assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                        assertThat(affordanceModel.getName()).isEqualTo("patch");
                        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.PATCH);
                        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/xyz");
                    });
                }
        );
    }

    @Test
    void toLink_basic() {
        var link = Affordances.of(methodOn(TestRestController.class).getTestResource("abc")).toLink()
                .withRel("testResource");

        assertThat(link.getHref()).isEqualTo("/test/abc");
        assertThat(link.getAffordances()).satisfiesExactly(
                affordance -> {
                    assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                        assertThat(affordanceModel.getName()).isEqualTo("getTestResource");
                        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.GET);
                        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/abc");
                    });
                }
        );
    }

    @Test
    void toLink_default_and_additional() {
        var link = Affordances.of(methodOn(TestRestController.class).getTestResource("abc"))
                .withDefault()
                .andAffordance(AffordanceCustomizer.afford(methodOn(TestRestController.class).patchTestResource("xyz", null)))
                .toLink()
                .withRel("testResource");

        assertThat(link.getHref()).isEqualTo("/test/abc");
        assertThat(link.getAffordances()).satisfiesExactly(
                affordance -> {
                    assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                        assertThat(affordanceModel.getName()).isEqualTo("default");
                        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.HEAD);
                        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/abc");
                    });
                },
                affordance -> {
                    assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                        assertThat(affordanceModel.getName()).isEqualTo("getTestResource");
                        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.GET);
                        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/abc");
                    });
                },
                affordance -> {
                    assertThat((AffordanceModel)affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON)).satisfies(affordanceModel -> {
                        assertThat(affordanceModel.getName()).isEqualTo("patchTestResource");
                        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.PATCH);
                        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/xyz");
                    });
                }
        );
    }
}