package com.contentgrid.hateoas.pagination.api;

import java.util.Comparator;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import lombok.NonNull;

public interface PaginationSystemLoader {

    static Optional<PaginationSystem> select(@NonNull PaginationParameters parameters) {
        return ServiceLoader.load(PaginationSystem.class).stream()
                .map(Provider::get)
                .sorted(Comparator.comparingInt(PaginationSystem::getPriority))
                .filter(system -> system.matches(parameters))
                .findFirst();
    }
}
