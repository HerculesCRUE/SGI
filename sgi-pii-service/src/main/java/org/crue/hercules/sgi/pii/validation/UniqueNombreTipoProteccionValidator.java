package org.crue.hercules.sgi.pii.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.TipoProteccion;
import org.crue.hercules.sgi.pii.model.TipoProteccion_;
import org.crue.hercules.sgi.pii.repository.TipoProteccionRepository;
import org.crue.hercules.sgi.pii.repository.specification.TipoProteccionSpecifications;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueNombreTipoProteccionValidator
    implements ConstraintValidator<UniqueNombreTipoProteccion, TipoProteccion> {
  private TipoProteccionRepository repository;
  private String field;
  private String subtipoProteccionMesage;

  public UniqueNombreTipoProteccionValidator(TipoProteccionRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(UniqueNombreTipoProteccion constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
    subtipoProteccionMesage = constraintAnnotation.subtipoProteccionMessage();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(TipoProteccion value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }

    final Boolean isSubtipo = value.getPadre() != null;
    Specification<TipoProteccion> specs = TipoProteccionSpecifications.activos();
    specs = specs.and((root, query, cb) -> {
      return (isSubtipo) ? cb.and(cb.isNotNull(root.get(TipoProteccion_.padre)),
          cb.equal(root.get(TipoProteccion_.padre), value.getPadre())) : cb.isNull(root.get(TipoProteccion_.padre));
    });
    specs = specs.and((root, query, cb) -> {
      return cb.equal(root.get(TipoProteccion_.nombre), value.getNombre());
    });

    List<TipoProteccion> tipoProteccion = repository.findAll(specs);
    boolean returnValue = !tipoProteccion.stream().anyMatch(tipo -> tipo.getId() != value.getId());

    if (!returnValue) {
      addEntityMessageParameter(context, isSubtipo);
    }
    return returnValue;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context, Boolean isSubtipo) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);

    final String errorMessage = isSubtipo ? ApplicationContextSupport.getMessage(this.subtipoProteccionMesage)
        : ApplicationContextSupport.getMessage(TipoProteccion.class);
    hibernateContext.addMessageParameter("entity", errorMessage);
    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }
}
