package com.contentgrid.hateoas.spring.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks components as part of the public API
 *
 * @see InternalApi for non-public components
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface PublicApi {

}
