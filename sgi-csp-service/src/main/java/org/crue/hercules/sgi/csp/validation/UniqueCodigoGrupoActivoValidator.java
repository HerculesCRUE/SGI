package org.crue.hercules.sgi.csp.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.repository.GrupoRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoSpecifications;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueCodigoGrupoActivoValidator implements ConstraintValidator<UniqueCodigoGrupoActivo, Grupo> {
  private static final String MESSAGE_PARAM_ENTITY = "entity";

  private final GrupoRepository repository;
  private String field;

  @Override
  public void initialize(UniqueCodigoGrupoActivo constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  public boolean isValid(Grupo value, ConstraintValidatorContext context) {
    if (value == null) {
      return false;
    }

    List<Grupo> grupos = repository
        .findAll(GrupoSpecifications.byCodigo(value.getCodigo()).and(GrupoSpecifications.activos()));
    boolean isCodigoDuplicado = grupos.stream().anyMatch(grupo -> !grupo.getId().equals(value.getId()));
    if (isCodigoDuplicado) {
      addEntityMessageParameter(context);
    }
    return !isCodigoDuplicado;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter(MESSAGE_PARAM_ENTITY, ApplicationContextSupport.getMessage(Grupo.class));
    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }

}
