package org.crue.hercules.sgi.csp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyecto.Orden;
import org.crue.hercules.sgi.csp.repository.RolProyectoRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueRolPrincipalOrdenPrimarioRolProyectoActivoValidator
    implements ConstraintValidator<UniqueRolPrincipalOrdenPrimarioRolProyectoActivo, RolProyecto> {

  private final RolProyectoRepository repository;

  private String field;

  @Override
  public void initialize(UniqueRolPrincipalOrdenPrimarioRolProyectoActivo constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  public boolean isValid(RolProyecto value, ConstraintValidatorContext context) {
    if (value.getNombre() == null) {
      return true;
    }

    boolean isValid = false;

    if (value.getId() != null) {
      isValid = this.repository.existsByOrdenAndIdNotAndRolPrincipalIsTrueAndActivoIsTrue(Orden.PRIMARIO,
          value.getId());
    } else {
      isValid = this.repository.existsByOrdenAndRolPrincipalIsTrueAndActivoIsTrue(Orden.PRIMARIO);
    }

    if (Boolean.TRUE.equals(isValid && value.getRolPrincipal() && value.getActivo())
        && (ObjectUtils.isNotEmpty(value.getOrden()) && value.getOrden().equals(Orden.PRIMARIO))) {
      this.addEntityMessageParameter(context);
      return false;
    }

    return true;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context
        .unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(RolProyecto.class));
    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }

}
