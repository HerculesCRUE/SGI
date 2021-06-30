package org.crue.hercules.sgi.eti.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Checks that the provided entity (found by id) has a field with the specified
 * value.
 */
@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldValueEqualsValidator.class)
public @interface FieldValueEquals {
  String message() default "{org.crue.hercules.sgi.eti.validation.FieldValueEquals.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  Class<?> entityClass();

  Class<?> repositoryClass() default void.class;

  String field();

  String value();
}
