package io.dexproject.achatservice.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, PARAMETER, TYPE, TYPE_USE, TYPE_PARAMETER, METHOD, CONSTRUCTOR, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = UniqueValidatorConstraint.class)
@Inherited
@ReportAsSingleViolation
public @interface CurrentCompany {
    String message() default "impossible de retouver la société encours";
}
