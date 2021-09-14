package org.crue.hercules.sgi.pii.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoOverlappedTramoRepartoActivoValidator.class)
public @interface NoOverlappedTramoRepartoActivo {
  String message() default "{org.crue.hercules.sgi.pii.validation.NoOverlappedTramoRepartoActivo.message}";

  String field() default "org.crue.hercules.sgi.pii.model.NoOverlappedTramoRepartoActivo.stretch";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
