package org.crue.hercules.sgi.csp.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.model.TipoHitoNombre;
import org.crue.hercules.sgi.csp.repository.TipoHitoRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueNombreTipoHitoActivoValidator
    implements ConstraintValidator<UniqueNombreTipoHitoActivo, TipoHito> {

  private TipoHitoRepository repository;
  private String field;

  public UniqueNombreTipoHitoActivoValidator(TipoHitoRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(UniqueNombreTipoHitoActivo constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(TipoHito value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }
    for (TipoHitoNombre nombreI18n : value.getNombre()) {
      Optional<TipoHito> tipoHito = repository
          .findByNombreLangAndNombreValueAndActivoIsTrue(nombreI18n.getLang(), nombreI18n.getValue());
      boolean returnValue = (!tipoHito.isPresent() || tipoHito.get().getId().equals(value.getId()));
      if (!returnValue) {
        addEntityMessageParameter(context, nombreI18n);
        return false;
      }
    }
    return true;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context, TipoHitoNombre nombreI18n) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("nombre", nombreI18n.getValue());

    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .enableExpressionLanguage(ExpressionLanguageFeatureLevel.BEAN_PROPERTIES)
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }
}
