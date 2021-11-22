package org.crue.hercules.sgi.pii.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueTipoTramoRepartoValidator.class)
public @interface UniqueTipoTramoReparto {
  String message() default "{org.crue.hercules.sgi.pii.validation.UniqueTipoTramoReparto.message}";

  String field() default "org.crue.hercules.sgi.pii.model.UniqueTipoTramoReparto.stretch";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
