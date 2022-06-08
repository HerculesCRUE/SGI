package org.crue.hercules.sgi.prc.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.dto.BaremoInput;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class BaremosNoRepetidoValidator implements ConstraintValidator<BaremosNoRepetido, List<BaremoInput>> {

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(List<BaremoInput> value, ConstraintValidatorContext context) {
    if (value == null) {
      return false;
    }
    final Long sizeDistintc = value.stream()
        .map(BaremoInput::getConfiguracionBaremoId)
        .distinct()
        .count();
    boolean returnValue = value.size() == sizeDistintc;
    if (!returnValue) {
      addEntityMessageParameter(context);
    }
    return returnValue;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(Baremo.class));
    hibernateContext.addMessageParameter("config", ApplicationContextSupport.getMessage(ConfiguracionBaremo.class));
  }
}
