package com.contentgrid.hateoas.spring.affordances;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
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

    private final InputPayloadMetadata delegate;
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
        return Stream.concat(delegate.stream(), propertyModifier.get())
                .filter(propertyModifier::test)
                .map(propertyModifier::apply);
    }

    @Override
    public <T extends Named> @NonNull T customize(@NonNull T target,
            @NonNull Function<PropertyMetadata, T> customizer) {
        return delegate.customize(target, propertyMetadata -> {
            if (propertyModifier.test(propertyMetadata)) {
                return target;
            }
            return customizer.apply(propertyModifier.apply(propertyMetadata));
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

    public interface PropertyModifier {
        PropertyMetadata apply(PropertyMetadata metadata);
        boolean test(PropertyMetadata metadata);
        Stream<PropertyMetadata> get();

        default PropertyModifier when(boolean condition) {
            return condition ? this : PropertyModifier.none();
        }

        /**
         * Removes a named property
         *
         * @param propertyName The name of the property to remove
         */
        static PropertyModifier drop(String propertyName) {
            return new DroppingPropertyModifier(propertyName);
        }

        /**
         * Modifies {@link PropertyMetadata} for a named property
         *
         * @see #addAllowedValues(String, List)
         * @param propertyName The name of the property to modify
         * @param customizer Returns a new {@link PropertyMetadata} that incorporates the requested changes
         */
        static PropertyModifier customize(String propertyName,
                UnaryOperator<PropertyMetadata> customizer) {
            return new CustomizingPropertyModifier(propertyName, customizer);
        }

        /**
         * Adds allowed values to a named property
         *
         * @param propertyName The name of the property to modify
         * @param allowedValues A list of option values for the property
         */
        static PropertyModifier addAllowedValues(String propertyName, List<?> allowedValues) {
            return customize(propertyName,
                    propertyMetadata -> new PropertyMetadataWithAllowedValues(propertyMetadata, allowedValues));
        }

        static PropertyModifier addSelectedValue(String propertyName, Object selectedValue) {
            return customize(propertyName, propertyMetadata -> new PropertyMetadataWithSelectedValue(propertyMetadata, selectedValue));
        }

        static PropertyModifier add(String propertyName, Class<?> type) {
            return add(propertyName, ResolvableType.forClass(type));
        }

        static PropertyModifier add(String propertyName, ResolvableType type) {
            return add(propertyName, type, Function.identity());
        }

        static PropertyModifier add(String propertyName, Class<?> type, Function<BasicPropertyMetadata, ? extends PropertyMetadata> customizer) {
            return add(propertyName, ResolvableType.forClass(type), customizer);
        }

        static PropertyModifier add(String propertyName, ResolvableType type, Function<BasicPropertyMetadata, ? extends PropertyMetadata> customizer) {
            return new InsertingPropertyModifier(customizer.apply(new BasicPropertyMetadata(propertyName, type)));
        }

        /**
         * @return
         */
        static PropertyModifier none() {
            return new NonePropertyModifier();
        }

    }

    @RequiredArgsConstructor
    private static class ComposedPropertyModifier implements PropertyModifier {

        private final PropertyModifier first;
        private final PropertyModifier second;

        @Override
        public PropertyMetadata apply(PropertyMetadata propertyMetadata) {
            return second.apply(first.apply(propertyMetadata));
        }

        @Override
        public boolean test(PropertyMetadata propertyMetadata) {
            return first.test(propertyMetadata) && second.test(propertyMetadata);
        }

        @Override
        public Stream<PropertyMetadata> get() {
            return Stream.concat(first.get(), second.get());
        }
    }

    private static class NonePropertyModifier implements PropertyModifier {

        @Override
        public PropertyMetadata apply(PropertyMetadata propertyMetadata) {
            return propertyMetadata;
        }

        @Override
        public boolean test(PropertyMetadata propertyMetadata) {
            return true;
        }

        @Override
        public Stream<PropertyMetadata> get() {
            return Stream.empty();
        }
    }

    @RequiredArgsConstructor
    private static class CustomizingPropertyModifier implements PropertyModifier {

        private final String propertyName;
        private final Function<PropertyMetadata, PropertyMetadata> customizer;

        @Override
        public PropertyMetadata apply(PropertyMetadata propertyMetadata) {
            if (propertyMetadata.hasName(propertyName)) {
                return customizer.apply(propertyMetadata);
            }
            return propertyMetadata;
        }

        @Override
        public boolean test(PropertyMetadata propertyMetadata) {
            return true;
        }

        @Override
        public Stream<PropertyMetadata> get() {
            return Stream.empty();
        }
    }

    @RequiredArgsConstructor
    private static class DroppingPropertyModifier implements PropertyModifier {

        private final String propertyName;

        @Override
        public PropertyMetadata apply(PropertyMetadata propertyMetadata) {
            return propertyMetadata;
        }

        @Override
        public boolean test(PropertyMetadata propertyMetadata) {
            return !propertyMetadata.hasName(propertyName);
        }

        @Override
        public Stream<PropertyMetadata> get() {
            return Stream.empty();
        }
    }

    @RequiredArgsConstructor
    private static class InsertingPropertyModifier implements PropertyModifier {
        private final PropertyMetadata metadata;

        @Override
        public PropertyMetadata apply(PropertyMetadata propertyMetadata) {
            return propertyMetadata;
        }

        @Override
        public boolean test(PropertyMetadata propertyMetadata) {
            return true;
        }

        @Override
        public Stream<PropertyMetadata> get() {
            return Stream.of(metadata);
        }
    }
}
