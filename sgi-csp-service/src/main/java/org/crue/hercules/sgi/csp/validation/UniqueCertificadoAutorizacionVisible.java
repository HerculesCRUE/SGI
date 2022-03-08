package org.crue.hercules.sgi.csp.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueCertificadoAutorizacionVisibleValidator.class)
public @interface UniqueCertificadoAutorizacionVisible {

  String message() default "{org.crue.hercules.sgi.csp.validation.UniqueCertificadoAutorizacionVisible.message}";

  String field() default "org.crue.hercules.sgi.csp.model.UniqueCertificadoAutorizacionVisible.name";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
