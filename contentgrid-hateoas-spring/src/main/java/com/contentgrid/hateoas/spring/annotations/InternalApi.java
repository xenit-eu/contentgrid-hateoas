package com.contentgrid.hateoas.spring.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks components that are not part of the public API
 *
 * @see PublicApi For components that are explicitly marked as public API
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface InternalApi {

}
