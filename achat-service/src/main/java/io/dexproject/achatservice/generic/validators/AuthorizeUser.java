package io.dexproject.achatservice.generic.validators;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AuthorizeUser {
    String actionKey();
}
