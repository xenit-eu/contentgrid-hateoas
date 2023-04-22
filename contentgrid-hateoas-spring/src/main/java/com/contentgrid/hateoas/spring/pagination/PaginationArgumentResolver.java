/*
 * Copyright 2016-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.contentgrid.hateoas.spring.pagination;

import com.contentgrid.hateoas.pagination.api.Pagination;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Argument resolver to extract a {@link Pagination} object from a {@link NativeWebRequest} for a particular
 * {@link MethodParameter}. {@link Pagination} resolution yields either
 * in a {@link Pagination} object or {@literal null} if {@link Pagination} cannot be resolved.
 *
 *
 * @author Mark Paluch
 * @author Toon Geens
 *
 * @see org.springframework.data.web.PageableArgumentResolver
 */
public interface PaginationArgumentResolver extends HandlerMethodArgumentResolver {

    /**
     * Resolves a {@link Pagination} method parameter into an argument value from a given request.
     *
     * @param parameter the method parameter to resolve. This parameter must have previously been passed to
     *          {@link #supportsParameter} which must have returned {@code true}.
     * @param mavContainer the ModelAndViewContainer for the current request
     * @param webRequest the current request
     * @param binderFactory a factory for creating {@link WebDataBinder} instances
     * @return the resolved argument value.
     */
    @NonNull
    @Override
    Pagination resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory);
}
