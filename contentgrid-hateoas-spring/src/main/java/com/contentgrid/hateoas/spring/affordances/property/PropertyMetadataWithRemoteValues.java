package com.contentgrid.hateoas.spring.affordances.property;

import com.contentgrid.hateoas.spring.annotations.InternalApi;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.hateoas.AffordanceModel.PropertyMetadata;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsOptions;

public class PropertyMetadataWithRemoteValues extends CustomPropertyMetadata implements Supplier<HalFormsOptions> {

    @Getter
    @NonNull
    private final Link link;

    @InternalApi
    public PropertyMetadataWithRemoteValues(PropertyMetadata delegate, Link link) {
        super(delegate);
        this.link = link;
    }

    @Override
    public HalFormsOptions get() {
        return HalFormsOptions.remote(link);
    }
}
