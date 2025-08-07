package com.contentgrid.hateoas.pagination.api;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.NonNull;

public class PaginationParameters {

    private final Map<String, List<String>> params;

    public PaginationParameters(Map<String, List<String>> parameters) {
        this.params = Map.copyOf(parameters);
    }

    public boolean containsKey(String key) {
        return this.params.containsKey(key);
    }

    public <T> T getValue(@NonNull String key, @NonNull Function<String, T> convert, T defaultValue) {
        var list = this.params.get(key);
        if (list == null || list.isEmpty()) {
            return defaultValue;
        }
        var value = list.get(0);
        if (value == null) {
            return defaultValue;
        }

        try {
            return convert.apply(value);
        } catch (RuntimeException ex) {
            return defaultValue;
        }
    }

    public <T> List<T> getValues(@NonNull String key, @NonNull Function<String, T> convert) {
        var list = this.params.get(key);
        if (list == null || list.isEmpty()) {
            return List.of();
        }

        try {
            return list.stream().map(convert).toList();
        } catch (RuntimeException ex) {
            return List.of();
        }
    }

    public Integer getInteger(String key, Integer defaultValue) {
        return this.getValue(key, Integer::parseInt, defaultValue);
    }

    public Long getLong(String key, Long defaultValue) {
        return this.getValue(key, Long::parseLong, defaultValue);
    }


}
