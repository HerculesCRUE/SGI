package org.crue.hercules.sgi.prc.validation;

import java.lang.annotation.Annotation;
import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

public abstract class AbstractEnumValueValidValidator<A extends Annotation> implements ConstraintValidator<A, String> {
  /** Message parameter holding the entity name */
  public static final String MESSAGE_PARAMETER_ENTITY = "entity";
  /** Message parameter holding the entity field value */
  public static final String MESSAGE_PARAMETER_VALUE = "value";

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (Objects.isNull(value)) {
      return true;
    }

    boolean isValid = isValueValid(value);
    if (!isValid) {
      addEntityMessageParameter(value, getEntityMessage(), context);
    }
    return isValid;
  }

  private void addEntityMessageParameter(String value, String entity, ConstraintValidatorContext context) {
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter(MESSAGE_PARAMETER_ENTITY, entity);
    hibernateContext.addMessageParameter(MESSAGE_PARAMETER_VALUE, value);
  }

  public abstract boolean isValueValid(String value);

  public abstract String getEntityMessage();

}
