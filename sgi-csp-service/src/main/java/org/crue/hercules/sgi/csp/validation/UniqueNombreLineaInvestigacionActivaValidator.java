package org.crue.hercules.sgi.csp.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.LineaInvestigacion;
import org.crue.hercules.sgi.csp.repository.LineaInvestigacionRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueNombreLineaInvestigacionActivaValidator
    implements ConstraintValidator<UniqueNombreLineaInvestigacionActiva, LineaInvestigacion> {
  private LineaInvestigacionRepository repository;

  public UniqueNombreLineaInvestigacionActivaValidator(LineaInvestigacionRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(LineaInvestigacion value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }
    Optional<LineaInvestigacion> lineaInvestigacion = repository.findByNombreAndActivoIsTrue(value.getNombre());
    boolean returnValue = (!lineaInvestigacion.isPresent() || lineaInvestigacion.get().getId().equals(value.getId()));
    if (!returnValue) {
      addEntityMessageParameter(context);
    }
    return returnValue;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(LineaInvestigacion.class));
  }
}
