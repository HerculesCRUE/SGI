package org.crue.hercules.sgi.prc.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.repository.ConvocatoriaBaremacionRepository;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueAnioConvocatoriaBaremacionActivaValidator
    implements ConstraintValidator<UniqueAnioConvocatoriaBaremacionActiva, ConvocatoriaBaremacion> {
  private ConvocatoriaBaremacionRepository repository;
  private String field;

  public UniqueAnioConvocatoriaBaremacionActivaValidator(ConvocatoriaBaremacionRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(UniqueAnioConvocatoriaBaremacionActiva constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(ConvocatoriaBaremacion value, ConstraintValidatorContext context) {
    if (value == null || value.getAnio() == null) {
      return false;
    }
    Optional<ConvocatoriaBaremacion> convocatoriaBaremacion = repository.findByAnioAndActivoIsTrue(value.getAnio());
    boolean returnValue = (!convocatoriaBaremacion.isPresent()
        || convocatoriaBaremacion.get().getId().equals(value.getId()));
    if (!returnValue) {
      addEntityMessageParameter(context);
    }
    return returnValue;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(ConvocatoriaBaremacion.class));
    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }
}
