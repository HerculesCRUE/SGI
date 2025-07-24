package org.crue.hercules.sgi.csp.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoEnlaceNombre;
import org.crue.hercules.sgi.csp.repository.TipoEnlaceRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueNombreTipoEnlaceActivoValidator
    implements ConstraintValidator<UniqueNombreTipoEnlaceActivo, TipoEnlace> {

  private final TipoEnlaceRepository repository;
  private String field;

  @Override
  public void initialize(UniqueNombreTipoEnlaceActivo constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(TipoEnlace value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }
    for (TipoEnlaceNombre nombre : value.getNombre()) {
      Optional<TipoEnlace> tipoEnlace = repository
          .findByNombreLangAndNombreValueAndActivoIsTrue(nombre.getLang(), nombre.getValue());
      boolean returnValue = (!tipoEnlace.isPresent()
          || tipoEnlace.get().getId().equals(value.getId()));
      if (!returnValue) {
        addEntityMessageParameter(context, nombre);
        return false;
      }
    }

    return true;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context, TipoEnlaceNombre nombreI18n) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(TipoEnlace.class));
    hibernateContext.addMessageParameter("nombre", nombreI18n.getValue());

    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .enableExpressionLanguage(ExpressionLanguageFeatureLevel.BEAN_PROPERTIES)
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }
}
