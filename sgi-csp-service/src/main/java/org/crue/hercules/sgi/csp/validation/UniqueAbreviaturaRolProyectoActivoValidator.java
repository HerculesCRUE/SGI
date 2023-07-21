package org.crue.hercules.sgi.csp.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.repository.RolProyectoRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueAbreviaturaRolProyectoActivoValidator
    implements ConstraintValidator<UniqueAbreviaturaRolProyectoActivo, RolProyecto> {

  private RolProyectoRepository repository;
  private String field;

  public UniqueAbreviaturaRolProyectoActivoValidator(RolProyectoRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(UniqueAbreviaturaRolProyectoActivo constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(RolProyecto value, ConstraintValidatorContext context) {
    if (value == null || value.getAbreviatura() == null) {
      return false;
    }
    Optional<RolProyecto> rolProyecto = repository.findByAbreviaturaAndActivoIsTrue(value.getAbreviatura());
    boolean returnValue = (!rolProyecto.isPresent() || rolProyecto.get().getId().equals(value.getId()));
    if (!returnValue) {
      addEntityMessageParameter(context);
    }
    return returnValue;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(RolProyecto.class));

    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }
}
