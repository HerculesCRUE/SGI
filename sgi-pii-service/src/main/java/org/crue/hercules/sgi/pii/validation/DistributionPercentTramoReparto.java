package org.crue.hercules.sgi.pii.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DistributionPercentTramoRepartoValidator.class)
public @interface DistributionPercentTramoReparto {
  String message() default "{org.crue.hercules.sgi.pii.validation.DistributionPercentTramoReparto.message}";

  String field() default "org.crue.hercules.sgi.pii.model.DistributionPercentTramoReparto.percentage";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
