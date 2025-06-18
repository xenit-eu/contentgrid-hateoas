package com.contentgrid.hateoas.spring.links;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.Builder;
import lombok.Singular;
import org.springframework.hateoas.Link;
import org.springframework.web.util.UriTemplate;

@Builder
public class UriTemplateMatcher<T> {
    @Singular
    private final List<Matcher<T>> matchers;

    public static <T> UriTemplateMatcherBuilder<T> builder() {
        return new UriTemplateMatcherBuilder<T>();
    }

    public static class UriTemplateMatcherBuilder<T> {

        public UriTemplateMatcherBuilder<T> matcherFor(UriTemplate uriTemplate, Function<Map<String, String>, T> convertor) {
            return matcher(new Matcher<>(uriTemplate, convertor));
        }

        public UriTemplateMatcherBuilder<T> matcherFor(Link link, Function<Map<String, String>, T> convertor) {
            return matcherFor(new UriTemplate(link.getHref()), convertor);
        }

        public UriTemplateMatcherBuilder<T> matcherFor(Object invocationValue, Function<Map<String, String>, T> convertor) {
            return matcherFor(linkTo(invocationValue).withSelfRel(), convertor);
        }
    }

    public Collection<String> getMatchingUriTemplates() {
        return matchers.stream()
                .map(Matcher::uriTemplate)
                .map(UriTemplate::toString)
                .toList();
    }

    public Optional<T> tryMatch(String uri) {
        for (var matcher : matchers) {
            var matchResult = matcher.tryMatch(uri);
            if(matchResult.isPresent()) {
                return matchResult;
            }
        }
        return Optional.empty();
    }

    private record Matcher<T>(
            UriTemplate uriTemplate,
            Function<Map<String, String>, T> convertor
    ) {
        public Optional<T> tryMatch(String uri) {
            if(uriTemplate.matches(uri)) {
                return Optional.ofNullable(convertor.apply(uriTemplate.match(uri)));
            }
            return Optional.empty();
        }
    }


}
