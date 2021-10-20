package org.crue.hercules.sgi.pii.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPeriodoTitularidadValidator.class)
public @interface ValidPeriodoTitularidad {

  String message() default "{org.crue.hercules.sgi.pii.validation.ValidPeriodoTitularidad.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
