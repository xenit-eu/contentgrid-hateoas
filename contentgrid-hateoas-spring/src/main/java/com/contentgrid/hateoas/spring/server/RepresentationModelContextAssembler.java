/*
 * Copyright 2012-2021 the original author or authors.
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
package com.contentgrid.hateoas.spring.server;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

/**
 * Variant on the standard {@link RepresentationModelAssembler}, that allows for some context information to augment
 * the entity.
 *
 * @param <T> the source entity type
 * @param <D> the target representation model type
 * @param <C> the context type
 */
public interface RepresentationModelContextAssembler<T, D extends RepresentationModel<?>, C> {

	/**
	 * Converts the given entity of type {@code T} into a {@code D}, which extends {@link RepresentationModel}.
	 *
	 * @param entity
	 * @param context
	 * @return
	 */
	D toModel(T entity, C context);

	/**
	 * Converts an {@link Iterable} or {@code T}s with context {@code C} into an {@link Iterable} of
	 * {@link RepresentationModel} and wraps them in a {@link CollectionModel} instance.
	 *
	 * @param entities must not be {@literal null}.
	 * @return {@link CollectionModel} containing {@code D}.
	 */
	default CollectionModel<D> toCollectionModel(Iterable<? extends T> entities, C context) {

		return StreamSupport.stream(entities.spliterator(), false) //
				.map(entity -> this.toModel(entity, context)) //
				.collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
	}

	default RepresentationModelAssembler<T, D> withContext(C context) {
		return new RepresentationModelAssembler<>() {

			@Override
			public D toModel(T entity) {
				return RepresentationModelContextAssembler.this.toModel(entity, context);
			}

			@Override
			public CollectionModel<D> toCollectionModel(Iterable<? extends T> entities) {
				return RepresentationModelContextAssembler.this.toCollectionModel(entities, context);
			}
		};
	}
}
