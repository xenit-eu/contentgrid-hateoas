package com.contentgrid.hateoas.spring.affordances;

import com.contentgrid.hateoas.spring.affordances.property.modifier.PropertyModifier;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
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
@RequiredArgsConstructor
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
        return new CustomInputPayloadMetadata(delegate,
                new ComposedPropertyModifier(this.propertyModifier, propertyModifier));
    }

    @Override
    public @NonNull Stream<PropertyMetadata> stream() {
        return Stream.concat(delegate.stream(), propertyModifier.addProperties())
                .filter(propertyModifier::keepProperty)
                .map(propertyModifier::customizeProperty);
    }

    @Override
    public <T extends Named> @NonNull T customize(@NonNull T target,
            @NonNull Function<PropertyMetadata, T> customizer) {
        return delegate.customize(target, propertyMetadata -> {
            if (propertyModifier.keepProperty(propertyMetadata)) {
                return target;
            }
            return customizer.apply(propertyModifier.customizeProperty(propertyMetadata));
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

    @RequiredArgsConstructor
    private static class ComposedPropertyModifier implements PropertyModifier {

        private final PropertyModifier first;
        private final PropertyModifier second;

        @Override
        public PropertyMetadata customizeProperty(PropertyMetadata propertyMetadata) {
            return second.customizeProperty(first.customizeProperty(propertyMetadata));
        }

        @Override
        public boolean keepProperty(PropertyMetadata propertyMetadata) {
            return first.keepProperty(propertyMetadata) && second.keepProperty(propertyMetadata);
        }

        @Override
        public Stream<PropertyMetadata> addProperties() {
            return Stream.concat(first.addProperties(), second.addProperties());
        }
    }

}
