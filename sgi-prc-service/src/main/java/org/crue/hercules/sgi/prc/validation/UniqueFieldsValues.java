package org.crue.hercules.sgi.prc.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Checks that the provided entity does not have a field with the specified
 * value.
 */
@Documented
@Constraint(validatedBy = { UniqueFieldsValuesValidator.class })
@Target({ TYPE, FIELD })
@Retention(RUNTIME)
public @interface UniqueFieldsValues {
  /**
   * Key for creating error messages
   * 
   * @return the key for creating error messages
   */
  String message() default "{org.crue.hercules.sgi.framework.validation.UniqueFieldsValues.message}";

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
   * The entity fields names whose value must be unique in the database.
   * 
   * @return the fields names
   */
  String[] fieldsNames();
}
