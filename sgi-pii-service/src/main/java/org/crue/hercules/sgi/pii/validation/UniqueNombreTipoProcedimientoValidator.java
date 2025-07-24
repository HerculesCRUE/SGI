package org.crue.hercules.sgi.pii.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.TipoProcedimiento;
import org.crue.hercules.sgi.pii.model.TipoProcedimientoNombre;
import org.crue.hercules.sgi.pii.repository.TipoProcedimientoRepository;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueNombreTipoProcedimientoValidator
    implements ConstraintValidator<UniqueNombreTipoProcedimiento, TipoProcedimiento> {

  private final TipoProcedimientoRepository tipoProcedimientoRepository;

  private String field;

  @Override
  public void initialize(UniqueNombreTipoProcedimiento constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  public boolean isValid(TipoProcedimiento value, ConstraintValidatorContext context) {

    if (value == null || value.getNombre() == null) {
      return false;
    }

    for (TipoProcedimientoNombre nombreI18n : value.getNombre()) {
      Optional<TipoProcedimiento> tipoProcedimiento = tipoProcedimientoRepository
          .findByNombreLangAndNombreValueAndActivoIsTrue(
              nombreI18n.getLang(), nombreI18n.getValue());
      boolean returnValue = (!tipoProcedimiento.isPresent() || tipoProcedimiento.get().getId().equals(value.getId()));
      if (!returnValue) {
        addEntityMessageParameter(context, nombreI18n);
        return false;
      }
    }

    return true;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context, TipoProcedimientoNombre nombreI18n) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(TipoProcedimiento.class));
    hibernateContext.addMessageParameter("nombre", nombreI18n.getValue());
    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .enableExpressionLanguage(ExpressionLanguageFeatureLevel.BEAN_PROPERTIES)
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }

}
