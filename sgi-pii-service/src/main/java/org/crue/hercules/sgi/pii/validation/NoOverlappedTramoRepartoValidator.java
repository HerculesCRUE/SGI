package org.crue.hercules.sgi.pii.validation;

import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.TramoReparto;
import org.crue.hercules.sgi.pii.model.TramoReparto.Tipo;
import org.crue.hercules.sgi.pii.repository.TramoRepartoRepository;
import org.crue.hercules.sgi.pii.repository.specification.TramoRepartoSpecifications;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class NoOverlappedTramoRepartoValidator implements ConstraintValidator<NoOverlappedTramoReparto, TramoReparto> {
  private TramoRepartoRepository repository;
  private String field;

  public NoOverlappedTramoRepartoValidator(TramoRepartoRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(NoOverlappedTramoReparto constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(TramoReparto value, ConstraintValidatorContext context) {
    if (value == null) {
      return false;
    }
    Specification<TramoReparto> spec;
    if (value.getTipo() == Tipo.FINAL) {
      if (value.getDesde() == null || value.getHasta() != null) {
        return false;
      }
      spec = TramoRepartoSpecifications.overlappedTramoRepartoFinal(value.getDesde());
    } else {
      if (value.getDesde() == null || value.getHasta() == null) {
        return false;
      }
      spec = TramoRepartoSpecifications.overlappedTramoRepartoNotFinal(value.getDesde(), value.getHasta());
    }

    List<TramoReparto> tramosReparto = repository.findAll(spec);
    boolean returnValue = tramosReparto.stream().filter(tramoReparto -> !tramoReparto.getId().equals(value.getId()))
        .count() == 0L;
    if (!returnValue) {
      addEntityMessageParameter(context);
    }
    return returnValue;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(TramoReparto.class));
    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }
}
