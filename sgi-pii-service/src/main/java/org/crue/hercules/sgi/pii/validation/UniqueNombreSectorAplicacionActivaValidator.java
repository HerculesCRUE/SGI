package org.crue.hercules.sgi.pii.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.SectorAplicacion;
import org.crue.hercules.sgi.pii.repository.SectorAplicacionRepository;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueNombreSectorAplicacionActivaValidator
    implements ConstraintValidator<UniqueNombreSectorAplicacionActiva, SectorAplicacion> {
  private SectorAplicacionRepository repository;

  public UniqueNombreSectorAplicacionActivaValidator(SectorAplicacionRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(SectorAplicacion value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }
    Optional<SectorAplicacion> sectorAplicacion = repository.findByNombreAndActivoIsTrue(value.getNombre());
    boolean returnValue = (!sectorAplicacion.isPresent() || sectorAplicacion.get().getId().equals(value.getId()));
    if (!returnValue) {
      addEntityMessageParameter(context);
    }
    return returnValue;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(SectorAplicacion.class));
  }
}
