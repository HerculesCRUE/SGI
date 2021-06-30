package org.crue.hercules.sgi.pii.validation;

import java.util.Optional;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.ResultadoInformePatentabilidad;
import org.crue.hercules.sgi.pii.repository.ResultadoInformePatentabilidadRepository;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueNombreResultadoInformePatentabilidadValidator
    implements ConstraintValidator<UniqueNombreResultadoInformePatentabilidad, ResultadoInformePatentabilidad> {

  private ResultadoInformePatentabilidadRepository repository;

  public UniqueNombreResultadoInformePatentabilidadValidator(ResultadoInformePatentabilidadRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(ResultadoInformePatentabilidad value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }
    Optional<ResultadoInformePatentabilidad> resultadoInformePatentabilidad = repository
        .findByNombreAndActivoIsTrue(value.getNombre());
    boolean returnValue = (!resultadoInformePatentabilidad.isPresent()
        || resultadoInformePatentabilidad.get().getId().equals(value.getId()));
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
        ApplicationContextSupport.getMessage(ResultadoInformePatentabilidad.class));
  }

}
