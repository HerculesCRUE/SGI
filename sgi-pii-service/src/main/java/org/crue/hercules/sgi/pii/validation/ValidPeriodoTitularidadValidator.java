package org.crue.hercules.sgi.pii.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidad;
import org.crue.hercules.sgi.pii.repository.PeriodoTitularidadRepository;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class ValidPeriodoTitularidadValidator
    implements ConstraintValidator<ValidPeriodoTitularidad, PeriodoTitularidad> {
  private PeriodoTitularidadRepository repository;

  public ValidPeriodoTitularidadValidator(PeriodoTitularidadRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(ValidPeriodoTitularidad constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(PeriodoTitularidad value, ConstraintValidatorContext context) {
    if (value == null || value.getFechaInicio() == null || value.getInvencionId() == null) {
      return returnValue(context, false);
    }

    Optional<PeriodoTitularidad> titularidadActivaOptional = this.repository
        .findByInvencionIdAndFechaFinIsNull(value.getInvencionId());

    if (titularidadActivaOptional.isPresent() && titularidadActivaOptional.get().getId() != value.getId()) {
      return returnValue(context, false);
    }

    Optional<PeriodoTitularidad> titularidadPosterior = this.repository
        .findByInvencionIdAndFechaFinGreaterThanEqual(value.getInvencionId(), value.getFechaInicio());
    if (titularidadPosterior.isPresent() && titularidadPosterior.get().getId() != value.getId()) {
      return returnValue(context, false);
    }

    return returnValue(context, true);
  }

  private Boolean returnValue(ConstraintValidatorContext context, Boolean result) {
    if (!result) {
      addEntityMessageParameter(context);
    }

    return result;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(PeriodoTitularidad.class));
  }

}
