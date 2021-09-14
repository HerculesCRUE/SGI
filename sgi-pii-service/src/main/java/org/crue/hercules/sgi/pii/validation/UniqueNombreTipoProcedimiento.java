package org.crue.hercules.sgi.pii.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueNombreTipoProcedimientoValidator.class)
public @interface UniqueNombreTipoProcedimiento {
  
  String message() default "{org.crue.hercules.sgi.pii.validation.UniqueNombreTipoProcedimiento.message}";

  String field() default "org.crue.hercules.sgi.pii.model.UniqueNombreTipoProcedimiento.name";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
