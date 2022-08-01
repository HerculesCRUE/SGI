package org.crue.hercules.sgi.csp.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.repository.ProyectoPeriodoJustificacionRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueIdentificadorJustificacionProyectoPeriodoJustificacionValidator implements
    ConstraintValidator<UniqueIdentificadorJustificacionProyectoPeriodoJustificacion, ProyectoPeriodoJustificacion> {
  private ProyectoPeriodoJustificacionRepository repository;
  private String field;

  public UniqueIdentificadorJustificacionProyectoPeriodoJustificacionValidator(
      ProyectoPeriodoJustificacionRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(UniqueIdentificadorJustificacionProyectoPeriodoJustificacion constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(ProyectoPeriodoJustificacion value, ConstraintValidatorContext context) {
    if (value == null || value.getIdentificadorJustificacion() == null) {
      return false;
    }
    Optional<ProyectoPeriodoJustificacion> proyectoPeriodoJustificacion = repository
        .findByIdentificadorJustificacion(value.getIdentificadorJustificacion());
    boolean returnValue = (!proyectoPeriodoJustificacion.isPresent()
        || proyectoPeriodoJustificacion.get().getId().equals(value.getId()));
    if (!returnValue) {
      addEntityMessageParameter(context);
    }
    return returnValue;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity",
        ApplicationContextSupport.getMessage(ProyectoPeriodoJustificacion.class));
    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }
}
