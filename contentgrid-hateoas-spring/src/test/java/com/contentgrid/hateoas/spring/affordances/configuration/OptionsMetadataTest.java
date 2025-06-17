package com.contentgrid.hateoas.spring.affordances.configuration;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.contentgrid.hateoas.spring.affordances.AffordanceCustomizer;
import com.contentgrid.hateoas.spring.affordances.Affordances;
import com.contentgrid.hateoas.spring.affordances.property.modifier.PropertyModifier;
import com.contentgrid.hateoas.spring.affordances.configuration.OptionsMetadataTest.TestController;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.With;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.hateoas.mediatype.hal.HalConfiguration;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(TestController.class)
class OptionsMetadataTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    public static class TestController {
        private final Map<RestResourceName, RestResource> resources = new HashMap<>() {{
            put(RestResourceName.of("jan"), new RestResource(RestResourceName.of("jan"), Department.IT));
            put(RestResourceName.of("nico"), new RestResource(RestResourceName.of("nico"), Department.FINANCE));

        }};
        @GetMapping("/test/{name}")
        public EntityModel<RestResource> getResource(@PathVariable RestResourceName name) {
            var resource = resources.get(name);
            if(resource == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            return EntityModel.of(resource)
                    .add(Affordances.of(methodOn(TestController.class).getResource(name))
                            .withDefault()
                            .andAffordances(
                                    Affordances.of(methodOn(TestController.class).deleteResource(name))
                                            .additionally(affordanceCustomizer -> affordanceCustomizer.configure(affordance -> affordance.withName("delete")))
                            )
                            .andAffordance(
                                    AffordanceCustomizer.afford(methodOn(TestController.class).updateResource(name, null))
                                            .configure(affordance -> affordance.withName("put"))
                                            .configureInput(PropertyModifier.addSelectedValue("department", resource.department))
                                            .configureInput(PropertyModifier.addAllowedValues("department",
                                                    Arrays.asList(Department.FINANCE, Department.IT)))
                            )
                            .toLink()
                            .withSelfRel()
                    );
        }

        @PostMapping("/test")
        public EntityModel<RestResource> createResource(@RequestBody RestResource resource) {
            resources.put(resource.name, resource);
            return getResource(resource.name);
        }

        @PutMapping("/test/{name}")
        EntityModel<RestResource> updateResource(@PathVariable RestResourceName name, @RequestBody RestResource resource) {
            resources.replace(name, resource.withName(name));
            return getResource(name);
        }

        @DeleteMapping("/test/{name}")
        ResponseEntity<?> deleteResource(@PathVariable RestResourceName name) {
            resources.remove(name);
            return ResponseEntity.ok().build();
        }

    }

    @AllArgsConstructor
    @Data
    @With
    static class RestResource {
        @NonNull
        @Setter(value = AccessLevel.NONE)
        RestResourceName name;
        @NonNull
        Department department;
    }

    @Value(staticConstructor = "of")
    static class RestResourceName {
        String value;

        @JsonValue
        public String toString() {
            return value;
        }
    }

    enum Department {
        IT,
        FINANCE,
        HR
    }

    @SpringBootApplication
    @EnableHypermediaSupport(type = {HypermediaType.HAL,HypermediaType.HAL_FORMS})
    @Import(TestController.class)
    static class App {
        @Bean
        HalFormsConfiguration halFormsConfiguration(HalConfiguration halConfiguration) {
            return new HalFormsConfiguration(halConfiguration)
                    .withOptions(RestResource.class, "department", new OptionsMetadata(Department.HR));
        }
    }

    @Test
    void optionsMetadataIsFollowed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test/jan").accept(MediaTypes.HAL_FORMS_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                           name: "jan",
                           department: "IT",
                           _links: {
                                self: {
                                    href: "http://localhost/test/jan"
                                }
                           },
                           _templates: {
                                "deleteResource": {
                                    method: "DELETE"
                                },
                                "delete": {
                                   method: "DELETE"
                               },
                               "put": {
                                    method: "PUT",
                                    properties: [
                                        {
                                            name: "name"
                                        },
                                        {
                                            name: "department",
                                            value: "IT",
                                            options: {
                                                "inline": ["FINANCE", "IT"]
                                            }
                                        }
                                    ]
                               }
                           }
                        }
                        """));

    }

}