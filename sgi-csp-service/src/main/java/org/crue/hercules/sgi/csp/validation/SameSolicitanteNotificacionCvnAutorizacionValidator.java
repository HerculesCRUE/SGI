package org.crue.hercules.sgi.csp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;
import org.crue.hercules.sgi.csp.repository.AutorizacionRepository;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SameSolicitanteNotificacionCvnAutorizacionValidator
    implements ConstraintValidator<SameSolicitanteNotificacionCvnAutorizacion, NotificacionProyectoExternoCVN> {

  private final AutorizacionRepository repository;

  private String field;

  @Override
  public void initialize(SameSolicitanteNotificacionCvnAutorizacion constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  public boolean isValid(NotificacionProyectoExternoCVN value, ConstraintValidatorContext context) {
    if (value.getAutorizacionId() == null) {
      return true;
    }

    boolean isValid = this.repository.existsByIdAndSolicitanteRef(value.getAutorizacionId(), value.getSolicitanteRef());

    if (!isValid) {
      this.addEntityMessageParameter(context);
    }
    return isValid;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context
        .unwrap(HibernateConstraintValidatorContext.class);
    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode(field).addConstraintViolation();
  }

}
