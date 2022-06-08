package org.crue.hercules.sgi.csp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;
import org.crue.hercules.sgi.csp.repository.NotificacionProyectoExternoCVNRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueRelationNotificacionCvnAutorizacionValidator
    implements ConstraintValidator<UniqueRelationNotificacionCvnAutorizacion, Long> {
  private static final String MESSAGE_PARAM_ENTITY = "entity";
  private static final String MESSAGE_PARAM_RELATED = "related";

  private final NotificacionProyectoExternoCVNRepository repository;

  @Override
  public void initialize(UniqueRelationNotificacionCvnAutorizacion constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(Long value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    boolean alreadyRelated = repository.existsByAutorizacionId(value);
    if (alreadyRelated) {
      addEntityMessageParameter(context);
    }
    return !alreadyRelated;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter(MESSAGE_PARAM_ENTITY,
        ApplicationContextSupport.getMessage(NotificacionProyectoExternoCVN.class));
    hibernateContext.addMessageParameter(MESSAGE_PARAM_RELATED,
        ApplicationContextSupport.getMessage(Autorizacion.class));

    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate());
  }

}
