package com.contentgrid.hateoas.spring.pagination;

import com.contentgrid.hateoas.pagination.api.Pagination;
import com.contentgrid.hateoas.pagination.api.PaginationParameters;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.core.MethodParameter;
import org.springframework.hateoas.server.mvc.UriComponentsContributor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.util.UriComponentsBuilder;



public class PaginationHandlerMethodArgumentResolver implements PaginationArgumentResolver, UriComponentsContributor {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pagination.class.equals(parameter.getParameterType());
    }

    @Override
    public Pagination resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        var parameterMap = webRequest.getParameterMap().entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> List.of(entry.getValue())
                ));
        var parameters = new PaginationParameters(parameterMap);

        return Pagination.from(parameters);
    }

    @Override
    public void enhance(UriComponentsBuilder builder, @Nullable MethodParameter parameter, Object value) {

        Assert.notNull(builder, "UriComponentsBuilder must not be null");

        if (value == null) {
            return;
        }
        
        if (!(value instanceof Pagination pagination)) {
            return;
        }

        if (pagination.isUnpaged()) {
            return;
        }

        pagination.getParameters().forEach((name, object) -> {
            if (object instanceof Collection<?> collection) {
                // use overload replaceQueryParam(String name, Collection<?> values)
                builder.replaceQueryParam(name, collection);
            } else {
                // use overload replaceQueryParam(String name, Object... values)
                builder.replaceQueryParam(name, object);
            }
        });

    }
}
