package org.crue.hercules.sgi.csp.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyectoNombre;
import org.crue.hercules.sgi.csp.repository.RolProyectoRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueNombreRolProyectoActivoValidator
    implements ConstraintValidator<UniqueNombreRolProyectoActivo, RolProyecto> {

  private RolProyectoRepository repository;
  private String field;

  public UniqueNombreRolProyectoActivoValidator(RolProyectoRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(UniqueNombreRolProyectoActivo constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(RolProyecto value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }
    for(RolProyectoNombre nombreI18n: value.getNombre()) {
      Optional<RolProyecto> rolProyecto = repository.findByNombreLangAndNombreValueAndActivoIsTrue(nombreI18n.getLang(), nombreI18n.getValue());
      boolean returnValue = (!rolProyecto.isPresent() || rolProyecto.get().getId().equals(value.getId()));
      if (!returnValue) {
        addEntityMessageParameter(context, nombreI18n);
        return false;
      }
    }
    return true;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context, RolProyectoNombre nombreI18n) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(RolProyecto.class));

    hibernateContext.addMessageParameter("nombre", nombreI18n.getValue());

    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .enableExpressionLanguage(ExpressionLanguageFeatureLevel.BEAN_PROPERTIES)
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }
}
