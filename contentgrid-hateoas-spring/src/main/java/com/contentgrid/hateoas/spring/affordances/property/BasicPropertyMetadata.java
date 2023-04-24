package com.contentgrid.hateoas.spring.affordances.property;

import com.contentgrid.hateoas.spring.annotations.PublicApi;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.With;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.mediatype.InputTypeFactory;
import org.springframework.lang.Nullable;

/**
 * Basic implementation of {@link PropertyMetadata} that allows customizations
 */
@Value
@With
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@PublicApi
public class BasicPropertyMetadata implements PropertyMetadata {
    private static final InputTypeFactory INPUT_TYPE_FACTORY;

    static {
        INPUT_TYPE_FACTORY = SpringFactoriesLoader.loadFactories(InputTypeFactory.class, BasicPropertyMetadata.class.getClassLoader()).get(0);
    }

    @NonNull
    String name;
    boolean required;
    boolean readOnly;
    @NonNull
    ResolvableType type;
    @Nullable
    String inputType;

    /**
     * @param propertyName The name of the property
     * @param type The type of the property
     */
    public BasicPropertyMetadata(String propertyName, ResolvableType type) {
        this(propertyName, false, true, type, INPUT_TYPE_FACTORY.getInputType(type.resolve(Object.class)));
    }

    @Override
    public Optional<String> getPattern() {
        return Optional.empty();
    }
}
