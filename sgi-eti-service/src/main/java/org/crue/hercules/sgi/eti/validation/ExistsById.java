package org.crue.hercules.sgi.eti.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistsByIdValidator.class)
public @interface ExistsById {
  String message() default "{org.crue.hercules.sgi.eti.validation.ExistsById.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  Class<?> entityClass();

  Class<?> repositoryClass() default void.class;
}
