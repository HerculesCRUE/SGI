package org.crue.hercules.sgi.prc.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = CamposCVNRequiredValidator.class)
@Documented
@Target({ TYPE, FIELD })
@Retention(RUNTIME)
public @interface CamposCVNRequired {
  String message() default "{org.hibernate.validator.constraints.CamposCVNRequired.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
