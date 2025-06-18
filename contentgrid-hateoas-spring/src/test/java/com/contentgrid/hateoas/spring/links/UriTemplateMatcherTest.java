package com.contentgrid.hateoas.spring.links;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.util.UriTemplate;

class UriTemplateMatcherTest {

    private final UriTemplateMatcher<Map<String, String>> uriTemplateMatcher = UriTemplateMatcher.<Map<String, String>>builder()
            .matcherFor(new UriTemplate("http://test.example.com/"), params -> params) // no params
            .matcherFor(new UriTemplate("http://test.example.com/items/{itemId}"), params -> params) // path param
            .matcherFor(new UriTemplate("http://test.example.com/items/{itemId}/{property}/{propertyId}"), params -> params) // multiple path params
            .build();

    static Stream<Arguments> validUris() {
        return Stream.of(
                Arguments.of("http://test.example.com/", Map.of()),
                Arguments.of("http://test.example.com/items/0", Map.of("itemId", "0")),
                Arguments.of("http://test.example.com/items/foo%40bar", Map.of("itemId", "foo%40bar")), // foo@bar
                Arguments.of("http://test.example.com/items/0/test/1", Map.of("itemId", "0", "property", "test", "propertyId", "1"))
        );
    }

    @ParameterizedTest
    @MethodSource
    void validUris(String uri, Map<String, String> expected) {
        assertThat(uriTemplateMatcher.tryMatch(uri)).hasValueSatisfying(actual -> {
            assertThat(actual).isEqualTo(expected);
        });
    }

    static Stream<String> invalidUris() {
        return Stream.of(
                "/items/0", // no origin
                "http://test.example.com", // missing '/'
                "https://test.example.com/items/0", // https instead of http
                "http://example.com/items/0", // invalid host
                "http://test.example.com/items/0/test", // missing propertyId
                "http://test.example.com/items/0/test/foo/bar" // path too long
        );
    }

    @ParameterizedTest
    @MethodSource
    void invalidUris(String uri) {
        assertThat(uriTemplateMatcher.tryMatch(uri)).isEmpty();
    }

}