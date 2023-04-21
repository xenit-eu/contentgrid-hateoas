package com.contentgrid.hateoas.spring.affordances;

import com.contentgrid.hateoas.spring.affordances.property.modifier.PropertyModifier;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ResolvableType;
import org.springframework.hateoas.AffordanceModel.InputPayloadMetadata;
import org.springframework.hateoas.AffordanceModel.Named;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.mediatype.PropertyUtils;
import org.springframework.http.MediaType;

/**
 * Custom {@link InputPayloadMetadata} that changes the {@link PropertyMetadata} returned from a delegate {@link InputPayloadMetadata}
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomInputPayloadMetadata implements InputPayloadMetadata {

    @NonNull
    private final InputPayloadMetadata delegate;
    @NonNull
    private final PropertyModifier propertyModifier;

    public static CustomInputPayloadMetadata from(Class<?> clazz) {
        return from(PropertyUtils.getExposedProperties(clazz));
    }

    public static CustomInputPayloadMetadata from(ResolvableType type) {
        return from(PropertyUtils.getExposedProperties(type));
    }

    public static CustomInputPayloadMetadata from(InputPayloadMetadata inputPayloadMetadata) {
        return new CustomInputPayloadMetadata(inputPayloadMetadata, PropertyModifier.none());
    }

    /**
     * Add an additional {@link PropertyModifier}
     *
     * @param propertyModifier The property modifier to add
     *
     * @return New {@link CustomInputPayloadMetadata} instance with an additional {@link PropertyModifier} applied to it
     */
    public CustomInputPayloadMetadata with(PropertyModifier propertyModifier) {
        return new CustomInputPayloadMetadata(delegate, this.propertyModifier.andThen(propertyModifier));
    }

    @Override
    public @NonNull Stream<PropertyMetadata> stream() {
        return propertyModifier.modify(delegate.stream());
    }

    @Override
    public <T extends Named> @NonNull T customize(@NonNull T target,
            @NonNull Function<PropertyMetadata, T> customizer) {
        return delegate.customize(target, propertyMetadata -> {
            var iterator = propertyModifier.modify(Stream.of(propertyMetadata)).iterator();
            var newTarget = target;
            while(iterator.hasNext()) {
                newTarget = customizer.apply(iterator.next());
            }
            return newTarget;
        });
    }

    @Override
    public @NonNull List<String> getI18nCodes() {
        return delegate.getI18nCodes();
    }

    @Override
    public @NonNull InputPayloadMetadata withMediaTypes(@NonNull List<MediaType> mediaType) {
        return new CustomInputPayloadMetadata(delegate.withMediaTypes(mediaType), propertyModifier);
    }

    @Override
    public @NonNull List<MediaType> getMediaTypes() {
        return delegate.getMediaTypes();
    }

    @Override
    public Class<?> getType() {
        return delegate.getType();
    }

}
