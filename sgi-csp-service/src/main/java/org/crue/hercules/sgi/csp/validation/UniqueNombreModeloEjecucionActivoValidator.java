package org.crue.hercules.sgi.csp.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.repository.ModeloEjecucionRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueNombreModeloEjecucionActivoValidator
    implements ConstraintValidator<UniqueNombreModeloEjecucionActivo, ModeloEjecucion> {

  private ModeloEjecucionRepository repository;
  private String field;

  public UniqueNombreModeloEjecucionActivoValidator(ModeloEjecucionRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(UniqueNombreModeloEjecucionActivo constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(ModeloEjecucion value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }
    for (ModeloEjecucionNombre nombreI18n : value.getNombre()) {
      Optional<ModeloEjecucion> modeloEjecucion = repository
          .findByNombreLangAndNombreValueAndActivoIsTrue(nombreI18n.getLang(), nombreI18n.getValue());
      boolean returnValue = (!modeloEjecucion.isPresent() || modeloEjecucion.get().getId().equals(value.getId()));
      if (!returnValue) {
        addEntityMessageParameter(context, nombreI18n);
        return false;
      }
    }
    return true;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context, ModeloEjecucionNombre nombreI18n) {
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
