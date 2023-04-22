package com.contentgrid.hateoas.pagination.api;

import java.util.Map;
import java.util.function.Function;
import lombok.NonNull;

public class PaginationParameters {

    private final Map<String, Object> params;

    public PaginationParameters(Map<String, ? extends Object> parameters) {
        this.params = Map.copyOf(parameters);
    }

    public boolean containsKey(String key) {
        return this.params.containsKey(key);
    }

    public <T> T getValue(@NonNull String key, @NonNull Function<String, T> convert, T defaultValue) {
        var value = this.params.get(key);
        if (value == null) {
            return defaultValue;
        }

        try {
            return convert.apply(String.valueOf(value));
        } catch (RuntimeException ex) {
            return defaultValue;
        }
    }

    public Integer getInteger(String key, Integer defaultValue) {
        return this.getValue(key, Integer::parseInt, defaultValue);
    }

    public Long getLong(String key, Long defaultValue) {
        return this.getValue(key, Long::parseLong, defaultValue);
    }


}
