package com.contengrid.hateoas.spring.affordances;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.contentgrid.hateoas.spring.affordances.AffordanceCustomizer;
import com.contentgrid.hateoas.spring.affordances.property.modifier.PropertyModifier;
import lombok.Data;
import lombok.NonNull;
import com.contengrid.hateoas.spring.affordances.AffordanceCustomizerTest.TestRestController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(TestRestController.class)
class AffordanceCustomizerTest {

    @RestController
    public static class TestRestController {

        @GetMapping("/test/{id}")
        @ResponseBody
        public TestRestResource get(@PathVariable String id) {
            return new TestRestResource("abc", "def");
        }

        @PatchMapping("/test/{id}")
        @ResponseBody
        public TestRestResource patch(@PathVariable String id, @RequestBody TestRestResourcePatch patch) {
            return new TestRestResource("abc", "def");
        }

    }

    @SpringBootApplication
    @EnableHypermediaSupport(type = {HypermediaType.HAL, HypermediaType.HAL_FORMS})
    static class Config {

    }

    @Data
    public static class TestRestResource {

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
        var standardAffordance = AffordanceCustomizer.afford(methodOn(TestRestController.class).patch("abc", null))
                .build();

        var affordanceModel = standardAffordance.getAffordanceModel(MediaTypes.HAL_FORMS_JSON);

        assertThat(affordanceModel.getName()).isEqualTo("patch");
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
        var affordance = AffordanceCustomizer.afford(methodOn(TestRestController.class).patch("abc", null))
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

}