package org.crue.hercules.sgi.prc.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.model.Baremo.TipoCuantia;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoNodo;
import org.crue.hercules.sgi.prc.repository.ConfiguracionBaremoRepository;
import org.crue.hercules.sgi.prc.repository.specification.ConfiguracionBaremoSpecifications;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class BaremoCuantiaValidator implements ConstraintValidator<BaremoCuantia, Baremo> {

  private ConfiguracionBaremoRepository configuracionBaremoRepository;

  public BaremoCuantiaValidator(ConfiguracionBaremoRepository configuracionBaremoRepository) {
    this.configuracionBaremoRepository = configuracionBaremoRepository;
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(Baremo value, ConstraintValidatorContext context) {
    if (value == null || value.getConfiguracionBaremoId() == null) {
      return false;
    }
    if (!isNodoCuantia(value.getConfiguracionBaremoId())) {
      return true;
    }
    boolean returnValue = isTipoCuantiaValid(value);
    if (!returnValue) {
      addEntityMessageParameter(context);
    }
    return returnValue;
  }

  private boolean isNodoCuantia(Long configuracionBaremoId) {
    return configuracionBaremoRepository
        .count(ConfiguracionBaremoSpecifications.activos()
            .and(ConfiguracionBaremoSpecifications.byIdAndTipoNodo(configuracionBaremoId,
                TipoNodo.PESO_CUANTIA))) > 0;
  }

  private boolean isTipoCuantiaValid(Baremo value) {
    // TipoCuantiaNode requires tipoCuantia field to be not null
    if (value.getTipoCuantia() == null) {
      return false;
    }
    return isTipoCuantiaPuntosValid(value) || isTipoCuantiaRangoValid(value);
  }

  private boolean isTipoCuantiaPuntosValid(Baremo value) {
    return value.getTipoCuantia() == TipoCuantia.PUNTOS && value.getCuantia() != null;
  }

  private boolean isTipoCuantiaRangoValid(Baremo value) {
    return value.getTipoCuantia() == TipoCuantia.RANGO && value.getCuantia() == null;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(Baremo.class));
    hibernateContext.addMessageParameter("config", ApplicationContextSupport.getMessage(ConfiguracionBaremo.class));
  }
}
