package org.crue.hercules.sgi.pii.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.ViaProteccion;
import org.crue.hercules.sgi.pii.model.ViaProteccion_;
import org.crue.hercules.sgi.pii.repository.ViaProteccionRepository;
import org.crue.hercules.sgi.pii.repository.specification.ViaProteccionSpecifications;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.data.jpa.domain.Specification;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueNombreViaProteccionValidator
    implements ConstraintValidator<UniqueNombreViaProteccion, ViaProteccion> {

  private final ViaProteccionRepository viaProteccionRepository;

  private String field;

  @Override
  public void initialize(UniqueNombreViaProteccion constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  public boolean isValid(ViaProteccion value, ConstraintValidatorContext context) {

    if (value == null || value.getNombre() == null) {
      return false;
    }

    Specification<ViaProteccion> specs = ViaProteccionSpecifications.activos();

    specs = specs.and((root, query, cb) -> {
      return cb.equal(root.get(ViaProteccion_.nombre), value.getNombre());
    });

    List<ViaProteccion> viaProteccion = viaProteccionRepository.findAll(specs);
    boolean hasNotExistingName = !viaProteccion.stream().anyMatch(via -> via.getId() != value.getId());

    if (!hasNotExistingName) {
      addEntityMessageParameter(context);
    }
    return hasNotExistingName;
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
