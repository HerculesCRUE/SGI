package org.crue.hercules.sgi.prc.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FirmaOrPersonaRefOrNombreAndApellidosAutorValidator.class)
public @interface FirmaOrPersonaRefOrNombreAndApellidosAutor {
  String message() default "{org.crue.hercules.sgi.prc.validation.FirmaOrPersonaRefOrNombreAndApellidosAutor.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
