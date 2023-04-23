package com.contengrid.hateoas.spring.affordances;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.contengrid.hateoas.spring.affordances.AffordanceCustomizerTest.TestRestController;
import com.contentgrid.hateoas.spring.affordances.AffordanceCustomizer;
import com.contentgrid.hateoas.spring.affordances.Affordances;
import com.contentgrid.hateoas.spring.affordances.property.modifier.PropertyModifier;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(TestRestController.class)
class AffordanceCustomizerTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    public static class TestRestController {
        private static final TestRestController testRestController = methodOn(TestRestController.class);

        @GetMapping("/test/{id}")
        @ResponseBody
        public TestRestResource getTestResource(@PathVariable String id) {
            return new TestRestResource("abc", "def")
                    .add(Affordances.of(testRestController.getTestResource(id))
                            .withDefault()
                            .andAffordance(AffordanceCustomizer.afford(testRestController.patchTestResource(id, null)).configure(affordance -> affordance.withName("patch")))
                            .toLink()
                            .withSelfRel()
                    );
        }

        @PatchMapping("/test/{id}")
        @ResponseBody
        public TestRestResource patchTestResource(@PathVariable String id, @RequestBody TestRestResourcePatch patch) {
            return new TestRestResource("abc", "def");
        }

    }

    @SpringBootApplication
    @EnableHypermediaSupport(type = {HypermediaType.HAL, HypermediaType.HAL_FORMS})
    @Import(TestRestController.class)
    static class Config {

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class TestRestResource extends RepresentationModel<TestRestResource> {

        @NonNull
        String title;

        @NonNull
        String name;

        boolean someFlag;

    }

    @Data
    public static class TestRestResourcePatch {

        String title;

        String name;
    }

    @Test
    void defaultAfford() {
        var standardAffordance = AffordanceCustomizer.afford(methodOn(TestRestController.class).patchTestResource("abc", null))
                .build();

        var affordanceModel = standardAffordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON);

        assertThat(affordanceModel.getName()).isEqualTo("patchTestResource");
        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.PATCH);
        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/abc");
        assertThat(affordanceModel.getInput().stream())
                .map(PropertyMetadata::getName)
                .containsExactlyInAnyOrder("title", "name");
        assertThat(affordanceModel.getOutput().stream())
                .map(PropertyMetadata::getName)
                .containsExactlyInAnyOrder("title", "name", "someFlag");
    }

    @Test
    void customizedAffordance() {
        var affordance = AffordanceCustomizer.afford(methodOn(TestRestController.class).patchTestResource("abc", null))
                .configure(configurableAffordance -> configurableAffordance.withName("update"))
                .configureInput(PropertyModifier.drop("name"))
                .build();

        var affordanceModel = affordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON);
        assertThat(affordanceModel.getName()).isEqualTo("update");
        assertThat(affordanceModel.getHttpMethod()).isEqualTo(HttpMethod.PATCH);
        assertThat(affordanceModel.getLink().getHref()).isEqualTo("/test/abc");
        assertThat(affordanceModel.getInput().stream())
                .map(PropertyMetadata::getName)
                .containsExactlyInAnyOrder("title");
        assertThat(affordanceModel.getOutput().stream())
                .map(PropertyMetadata::getName)
                .containsExactlyInAnyOrder("title", "name", "someFlag");
    }

    @Test
    void customizedAffordanceFromWeb() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test/abc")
                .accept(MediaTypes.HAL_FORMS_JSON)
        ).andExpectAll(
                MockMvcResultMatchers.status().is2xxSuccessful(),
                MockMvcResultMatchers.content()
                        .json("""
                        {
                            title: "abc",
                            name: "def",
                            someFlag: false,
                            _links: {
                                self: {
                                    href: "http://localhost/test/abc"
                                }
                            },
                            _templates: {
                                default: {
                                    method: "HEAD"
                                },
                                patch: {
                                    method: "PATCH",
                                    properties: [
                                        {
                                            name: "name",
                                            type: "text"
                                        },
                                        {
                                            name: "title",
                                            type: "text"
                                        }
                                    ]
                                }
                            }
                        }
                        """)
        );
    }

}