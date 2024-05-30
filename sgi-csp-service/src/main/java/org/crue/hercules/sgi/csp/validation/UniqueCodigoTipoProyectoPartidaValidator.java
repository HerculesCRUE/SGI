package org.crue.hercules.sgi.csp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.Configuracion;
import org.crue.hercules.sgi.csp.model.ProyectoPartida;
import org.crue.hercules.sgi.csp.repository.ConfiguracionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoPartidaRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueCodigoTipoProyectoPartidaValidator
    implements ConstraintValidator<UniqueCodigoTipoProyectoPartida, ProyectoPartida> {

  private final ProyectoPartidaRepository proyectoPartidaRepository;
  private final ConfiguracionRepository configuracionRepository;

  private String field;

  @Override
  public void initialize(UniqueCodigoTipoProyectoPartida constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  public boolean isValid(ProyectoPartida value, ConstraintValidatorContext context) {

    Configuracion config = configuracionRepository.findFirstByOrderByIdAsc().orElse(null);
    if (config == null) {
      return false;
    }

    boolean isValid = true;
    if (Boolean.TRUE.equals(config.getPartidasPresupuestariasSGE())) {
      isValid = !this.proyectoPartidaRepository.existsByProyectoIdAndPartidaRefAndTipoPartidaAndIdNot(
          value.getProyectoId(), value.getPartidaRef(), value.getTipoPartida(),
          value.getId() != null ? value.getId() : 0);
    } else {
      isValid = !this.proyectoPartidaRepository.existsByProyectoIdAndCodigoAndTipoPartidaAndIdNot(
          value.getProyectoId(), value.getCodigo(), value.getTipoPartida(), value.getId() != null ? value.getId() : 0);
    }

    if (!isValid) {
      this.addEntityMessageParameter(context);
    }
    return isValid;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context
        .unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity",
        ApplicationContextSupport.getMessage(ProyectoPartida.class));
    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }

}
