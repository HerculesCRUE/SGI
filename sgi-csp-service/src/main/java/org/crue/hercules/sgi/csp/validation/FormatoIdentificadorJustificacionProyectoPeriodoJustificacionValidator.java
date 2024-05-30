package org.crue.hercules.sgi.csp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.Configuracion;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.service.ConfiguracionService;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

public class FormatoIdentificadorJustificacionProyectoPeriodoJustificacionValidator
    implements
    ConstraintValidator<FormatoIdentificadorJustificacionProyectoPeriodoJustificacion, ProyectoPeriodoJustificacion> {
  private final ConfiguracionService configuracionService;
  private String field;

  public FormatoIdentificadorJustificacionProyectoPeriodoJustificacionValidator(
      ConfiguracionService configuracionService) {
    this.configuracionService = configuracionService;
  }

  @Override
  public void initialize(FormatoIdentificadorJustificacionProyectoPeriodoJustificacion constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(ProyectoPeriodoJustificacion value, ConstraintValidatorContext context) {
    if (value == null || StringUtils.isEmpty(value.getIdentificadorJustificacion())) {
      return true;
    }
    Configuracion configuracion = configuracionService.findConfiguracion();

    boolean returnValue = value.getIdentificadorJustificacion()
        .matches(configuracion.getFormatoIdentificadorJustificacion());

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
