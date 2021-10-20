package org.crue.hercules.sgi.pii.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.SolicitudProteccion;
import org.crue.hercules.sgi.pii.model.ViaProteccion;
import org.crue.hercules.sgi.pii.repository.SolicitudProteccionRepository;
import org.crue.hercules.sgi.pii.repository.ViaProteccionRepository;
import org.crue.hercules.sgi.pii.repository.specification.SolicitudProteccionSpecifications;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueSolicitudViaProteccionValidator
    implements ConstraintValidator<UniqueSolicitudViaProteccion, SolicitudProteccion> {

  private final SolicitudProteccionRepository solicitudProteccionRepository;
  private final ViaProteccionRepository viaProteccionRepository;

  private String field;

  @Override
  public void initialize(UniqueSolicitudViaProteccion constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isValid(SolicitudProteccion value, ConstraintValidatorContext context) {

    Specification<SolicitudProteccion> specs = null;
    ViaProteccion via = this.viaProteccionRepository.getOne(value.getViaProteccion().getId());
    if (via.getPaisEspecifico()) {
      specs = SolicitudProteccionSpecifications.solicitudesByViaProteccionPaisEspecifico(value.getId(),
          value.getInvencion().getId(), via.getId(), value.getPaisProteccionRef());
    } else {
      specs = SolicitudProteccionSpecifications.solicitudesByViaProteccion(value.getId(), value.getInvencion().getId(),
          via.getId());
    }
    List<SolicitudProteccion> solicitudes = solicitudProteccionRepository.findAll(specs);
    if (solicitudes.isEmpty()) {
      return true;
    }
    addEntityMessageParameter(context);

    return false;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(ViaProteccion.class));
    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }

}
