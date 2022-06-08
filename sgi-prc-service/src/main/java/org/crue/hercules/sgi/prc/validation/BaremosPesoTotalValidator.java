package org.crue.hercules.sgi.prc.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.collections4.ListUtils;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.dto.BaremoInput;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class BaremosPesoTotalValidator implements ConstraintValidator<BaremosPesoTotal, List<BaremoInput>> {

  private static final Integer REQUIRED_PESO_TOTAL = 100;

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(List<BaremoInput> value, ConstraintValidatorContext context) {
    final Integer pesoTotal = ListUtils.emptyIfNull(value).stream()
        .filter(baremo -> baremo.getPeso() != null)
        .map(BaremoInput::getPeso)
        .reduce(Integer::sum).orElse(0);
    boolean returnValue = REQUIRED_PESO_TOTAL.equals(pesoTotal);
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
    hibernateContext.addMessageParameter("quantity", REQUIRED_PESO_TOTAL);
  }
}
