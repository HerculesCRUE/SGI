package org.crue.hercules.sgi.prc.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo;
import org.crue.hercules.sgi.prc.repository.ConfiguracionBaremoRepository;
import org.crue.hercules.sgi.prc.repository.specification.ConfiguracionBaremoSpecifications;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class BaremoPuntosValidator implements ConstraintValidator<BaremoPuntos, Baremo> {

  private ConfiguracionBaremoRepository configuracionBaremoRepository;

  public BaremoPuntosValidator(ConfiguracionBaremoRepository configuracionBaremoRepository) {
    this.configuracionBaremoRepository = configuracionBaremoRepository;
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(Baremo value, ConstraintValidatorContext context) {
    if (value == null || value.getConfiguracionBaremoId() == null) {
      return false;
    }
    if (!isNodoPuntos(value.getConfiguracionBaremoId())) {
      return true;
    }
    boolean returnValue = value.getPuntos() != null;
    if (!returnValue) {
      addEntityMessageParameter(context);
    }
    return returnValue;
  }

  private boolean isNodoPuntos(Long configuracionBaremoId) {
    return configuracionBaremoRepository
        .count(ConfiguracionBaremoSpecifications.activos()
            .and(ConfiguracionBaremoSpecifications.byIdAndTipoNodoIn(configuracionBaremoId,
                ConfiguracionBaremo.TIPO_NODOS_PUNTOS))) > 0;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(Baremo.class));
    hibernateContext.addMessageParameter("config", ApplicationContextSupport.getMessage(ConfiguracionBaremo.class));
  }
}
