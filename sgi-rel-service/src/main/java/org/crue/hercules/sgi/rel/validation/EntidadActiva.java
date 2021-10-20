package org.crue.hercules.sgi.rel.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EntidadActivaValidator.class)
public @interface EntidadActiva {
  String message() default "{org.crue.hercules.sgi.rel.validation.EntidadActiva.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  Class<?> entityClass();

  Class<?> repositoryClass() default void.class;
}
