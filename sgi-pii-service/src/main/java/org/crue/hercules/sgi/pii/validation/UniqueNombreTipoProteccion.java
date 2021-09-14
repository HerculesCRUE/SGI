package org.crue.hercules.sgi.pii.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueNombreTipoProteccionValidator.class)
public @interface UniqueNombreTipoProteccion {

  String message() default "{org.crue.hercules.sgi.pii.validation.UniqueNombreTipoProteccion.message}";

  String field() default "org.crue.hercules.sgi.pii.model.UniqueNombreTipoProteccion.name";

  String subtipoProteccionMessage() default "org.crue.hercules.sgi.pii.model.TipoProteccion.subtipo.message";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
