package org.crue.hercules.sgi.prc.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BaremosPesoTotalValidator.class)
public @interface BaremosPesoTotal {
  String message() default "{org.crue.hercules.sgi.prc.validation.BaremosPesoTotal.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
