package org.crue.hercules.sgi.csp.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion;
import org.crue.hercules.sgi.csp.repository.TipoOrigenFuenteFinanciacionRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueNombreTipoOrigenFuenteFinanciacionActivaValidator
    implements ConstraintValidator<UniqueNombreTipoOrigenFuenteFinanciacionActiva, TipoOrigenFuenteFinanciacion> {
  private TipoOrigenFuenteFinanciacionRepository repository;
  private String field;

  public UniqueNombreTipoOrigenFuenteFinanciacionActivaValidator(TipoOrigenFuenteFinanciacionRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(UniqueNombreTipoOrigenFuenteFinanciacionActiva constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(TipoOrigenFuenteFinanciacion value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }
    Optional<TipoOrigenFuenteFinanciacion> tipoOrigenFuenteFinanciacion = repository
        .findByNombreAndActivoIsTrue(value.getNombre());
    boolean returnValue = (!tipoOrigenFuenteFinanciacion.isPresent()
        || tipoOrigenFuenteFinanciacion.get().getId().equals(value.getId()));
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
        ApplicationContextSupport.getMessage(TipoOrigenFuenteFinanciacion.class));

    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }
}
