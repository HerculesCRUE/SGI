package org.crue.hercules.sgi.csp.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFaseNombre;
import org.crue.hercules.sgi.csp.repository.TipoFaseRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueNombreTipoFaseActivoValidator
    implements ConstraintValidator<UniqueNombreTipoFaseActivo, TipoFase> {

  private TipoFaseRepository repository;
  private String field;

  public UniqueNombreTipoFaseActivoValidator(TipoFaseRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(UniqueNombreTipoFaseActivo constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(TipoFase value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }
    for (TipoFaseNombre nombreI18n : value.getNombre()) {
      Optional<TipoFase> tipoFase = repository
          .findByNombreLangAndNombreValueAndActivoIsTrue(nombreI18n.getLang(), nombreI18n.getValue());
      boolean returnValue = (!tipoFase.isPresent() || tipoFase.get().getId().equals(value.getId()));
      if (!returnValue) {
        addEntityMessageParameter(context, nombreI18n);
        return false;
      }
    }
    return true;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context, TipoFaseNombre nombreI18n) {
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
