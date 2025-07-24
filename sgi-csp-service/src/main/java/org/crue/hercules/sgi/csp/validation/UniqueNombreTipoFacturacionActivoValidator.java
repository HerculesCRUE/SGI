package org.crue.hercules.sgi.csp.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.TipoFacturacion;
import org.crue.hercules.sgi.csp.model.TipoFacturacionNombre;
import org.crue.hercules.sgi.csp.repository.TipoFacturacionRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueNombreTipoFacturacionActivoValidator
    implements ConstraintValidator<UniqueNombreTipoFacturacionActivo, TipoFacturacion> {

  private final TipoFacturacionRepository repository;
  private String field;

  @Override
  public void initialize(UniqueNombreTipoFacturacionActivo constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(TipoFacturacion value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }
    for (TipoFacturacionNombre nombre : value.getNombre()) {
      Optional<TipoFacturacion> tipoFacturacion = repository
          .findByNombreLangAndNombreValueAndActivoIsTrue(nombre.getLang(), nombre.getValue());
      boolean returnValue = (!tipoFacturacion.isPresent()
          || tipoFacturacion.get().getId().equals(value.getId()));
      if (!returnValue) {
        addEntityMessageParameter(context, nombre);
        return false;
      }
    }

    return true;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context,
      TipoFacturacionNombre tipoFacturacionNombre) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(TipoFacturacion.class));
    hibernateContext.addMessageParameter("nombre", tipoFacturacionNombre.getValue());

    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .enableExpressionLanguage(ExpressionLanguageFeatureLevel.BEAN_PROPERTIES)
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }
}
