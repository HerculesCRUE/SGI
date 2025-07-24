package org.crue.hercules.sgi.csp.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeograficoNombre;
import org.crue.hercules.sgi.csp.repository.TipoAmbitoGeograficoRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueNombreTipoAmbitoGeograficoActivoValidator
    implements ConstraintValidator<UniqueNombreTipoAmbitoGeograficoActivo, TipoAmbitoGeografico> {

  private TipoAmbitoGeograficoRepository repository;
  private String field;

  public UniqueNombreTipoAmbitoGeograficoActivoValidator(TipoAmbitoGeograficoRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(UniqueNombreTipoAmbitoGeograficoActivo constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(TipoAmbitoGeografico value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }
    for(TipoAmbitoGeograficoNombre ambitoGeograficoNombre: value.getNombre()){
      Optional<TipoAmbitoGeografico> tipoAmbitoGeografico = repository.findByNombreLangAndNombreValueAndActivoIsTrue(ambitoGeograficoNombre.getLang(), ambitoGeograficoNombre.getValue());
      boolean returnValue = (!tipoAmbitoGeografico.isPresent()
        || tipoAmbitoGeografico.get().getId().equals(value.getId()));
      if (!returnValue) {
        addEntityMessageParameter(context, ambitoGeograficoNombre);
        return false;
      }
    }

    return true;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context, TipoAmbitoGeograficoNombre ambitoGeograficoNombre) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(TipoAmbitoGeografico.class));
    hibernateContext.addMessageParameter("nombre", ambitoGeograficoNombre.getValue());

    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .enableExpressionLanguage(ExpressionLanguageFeatureLevel.BEAN_PROPERTIES)
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }
}
