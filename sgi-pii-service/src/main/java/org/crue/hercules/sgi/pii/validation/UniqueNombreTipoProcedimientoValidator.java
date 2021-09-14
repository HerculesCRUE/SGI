package org.crue.hercules.sgi.pii.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.TipoProcedimiento;
import org.crue.hercules.sgi.pii.model.TipoProcedimiento_;
import org.crue.hercules.sgi.pii.repository.TipoProcedimientoRepository;
import org.crue.hercules.sgi.pii.repository.specification.TipoProcedimientoSpecifications;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueNombreTipoProcedimientoValidator
    implements ConstraintValidator<UniqueNombreTipoProcedimiento, TipoProcedimiento> {

  private final TipoProcedimientoRepository tipoProcedimientoRepository;

  private String field;

  @Override
  public void initialize(UniqueNombreTipoProcedimiento constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  public boolean isValid(TipoProcedimiento value, ConstraintValidatorContext context) {

    if (value == null || StringUtils.isEmpty(value.getNombre())) {
      return false;
    }

    Specification<TipoProcedimiento> specs = TipoProcedimientoSpecifications.activos();

    specs = specs.and((root, query, cb) -> {
      return cb.equal(root.get(TipoProcedimiento_.nombre), value.getNombre());
    });

    List<TipoProcedimiento> tiposProcedimiento = this.tipoProcedimientoRepository.findAll(specs);
    boolean isRepeatedNombre = tiposProcedimiento.stream().anyMatch(tipo -> tipo.getId() != value.getId());

    if (isRepeatedNombre) {
      addEntityMessageParameter(context);
    }
    return !isRepeatedNombre;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(TipoProcedimiento.class));
    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }

}
