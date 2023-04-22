package com.contentgrid.hateoas.spring.pagination;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
class PaginationMetadata {

    @JsonProperty
    private Integer size;

    @JsonProperty
    private Long page;
}
