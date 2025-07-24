package org.crue.hercules.sgi.pii.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.ResultadoInformePatentabilidad;
import org.crue.hercules.sgi.pii.model.ResultadoInformePatentabilidadNombre;
import org.crue.hercules.sgi.pii.repository.ResultadoInformePatentabilidadRepository;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueNombreResultadoInformePatentabilidadValidator
    implements ConstraintValidator<UniqueNombreResultadoInformePatentabilidad, ResultadoInformePatentabilidad> {

  private ResultadoInformePatentabilidadRepository repository;
  private String field;

  public UniqueNombreResultadoInformePatentabilidadValidator(ResultadoInformePatentabilidadRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(UniqueNombreResultadoInformePatentabilidad constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(ResultadoInformePatentabilidad value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }
    for (ResultadoInformePatentabilidadNombre nombreI18n : value.getNombre()) {
      Optional<ResultadoInformePatentabilidad> resultadoInformePatentabilidad = repository
          .findByNombreLangAndNombreValueAndActivoIsTrue(nombreI18n.getLang(), nombreI18n.getValue());
      boolean returnValue = (!resultadoInformePatentabilidad.isPresent()
          || resultadoInformePatentabilidad.get().getId().equals(value.getId()));
      if (!returnValue) {
        addEntityMessageParameter(context, nombreI18n);
        return false;
      }
    }
    return true;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context,
      ResultadoInformePatentabilidadNombre nombreI18n) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity",
        ApplicationContextSupport.getMessage(ResultadoInformePatentabilidad.class));
    hibernateContext.addMessageParameter("nombre", nombreI18n.getValue());

    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .enableExpressionLanguage(ExpressionLanguageFeatureLevel.BEAN_PROPERTIES)
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }

}
