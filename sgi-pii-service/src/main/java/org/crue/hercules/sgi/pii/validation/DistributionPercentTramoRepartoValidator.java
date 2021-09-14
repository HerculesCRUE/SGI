package org.crue.hercules.sgi.pii.validation;

import java.math.BigDecimal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.TramoReparto;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class DistributionPercentTramoRepartoValidator
    implements ConstraintValidator<DistributionPercentTramoReparto, TramoReparto> {
  private static final BigDecimal MAX_PERCENTAGE = new BigDecimal("100");

  private String field;

  @Override
  public void initialize(DistributionPercentTramoReparto constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(TramoReparto value, ConstraintValidatorContext context) {
    if (value == null || value.getPorcentajeUniversidad() == null || value.getPorcentajeInventores() == null) {
      return false;
    }

    boolean returnValue = isDistribtuionPercentEqualsToMaxPercentage(value);
    if (!returnValue) {
      addEntityMessageParameter(context);
    }
    return returnValue;
  }

  private boolean isDistribtuionPercentEqualsToMaxPercentage(TramoReparto value) {
    return MAX_PERCENTAGE.compareTo(value.getPorcentajeUniversidad().add(value.getPorcentajeInventores())) == 0;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(TramoReparto.class));
    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }
}
