package org.crue.hercules.sgi.csp.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.model.RolSocioAbreviatura;
import org.crue.hercules.sgi.csp.repository.RolSocioRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueAbreviaturaRolSocioActivoValidator
    implements ConstraintValidator<UniqueAbreviaturaRolSocioActivo, RolSocio> {

  private RolSocioRepository repository;
  private String field;

  public UniqueAbreviaturaRolSocioActivoValidator(RolSocioRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(UniqueAbreviaturaRolSocioActivo constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(RolSocio value, ConstraintValidatorContext context) {
    if (value == null || value.getAbreviatura() == null) {
      return false;
    }
    for (RolSocioAbreviatura rolSocioAbreviatura : value.getAbreviatura()) {
      Optional<RolSocio> rolSocio = repository.findByAbreviaturaLangAndAbreviaturaValueAndActivoIsTrue(rolSocioAbreviatura.getLang(), rolSocioAbreviatura.getValue());
      boolean returnValue = (!rolSocio.isPresent() || rolSocio.get().getId().equals(value.getId()));
      if (!returnValue) {
        addEntityMessageParameter(context, rolSocioAbreviatura);
        return false;
      }
    }
    return true;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context, RolSocioAbreviatura rolSocioAbreviatura) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(RolSocio.class));
    hibernateContext.addMessageParameter("abreviatura", rolSocioAbreviatura.getValue());

    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .enableExpressionLanguage(ExpressionLanguageFeatureLevel.BEAN_PROPERTIES)
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }
}
