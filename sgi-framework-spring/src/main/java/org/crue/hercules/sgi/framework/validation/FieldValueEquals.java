package org.crue.hercules.sgi.framework.validation;

import java.lang.annotation.Documented;
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
@Documented
@Constraint(validatedBy = {})
@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldValueEquals {
  /**
   * Key for creating error messages
   * 
   * @return the key for creating error messages
   */
  String message() default "{org.crue.hercules.sgi.framework.validation.FieldValueEquals.message}";

  /**
   * Validation groups for our constraints
   * 
   * @return the validation groups for our constraints
   */
  Class<?>[] groups() default {};

  /**
   * Can be used by clients of the Bean Validation API to assign custom payload
   * objects to a constraint
   * 
   * @return the payload objects
   */
  Class<? extends Payload>[] payload() default {};

  /**
   * The {@link javax.persistence.Entity} class to check.
   * 
   * @return the {@link javax.persistence.Entity} class
   */
  Class<?> entityClass();

  /**
   * The entity field name to be checked.
   * 
   * @return the field name
   */
  String fieldName();

  /**
   * The value to check against.
   * 
   * @return the value to check
   */
  String value();
}
