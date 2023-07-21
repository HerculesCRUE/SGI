package org.crue.hercules.sgi.csp.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueAbreviaturaRolProyectoActivoValidator.class)
public @interface UniqueAbreviaturaRolProyectoActivo {
  String message() default "{org.crue.hercules.sgi.csp.validation.UniqueAbreviaturaRolProyectoActivo.message}";

  String field() default "abbreviation";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
