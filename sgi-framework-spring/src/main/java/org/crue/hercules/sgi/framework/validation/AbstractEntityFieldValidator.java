package org.crue.hercules.sgi.framework.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

/**
 * Base class for database driven field level entity validator.
 */
public abstract class AbstractEntityFieldValidator<A extends Annotation, T> extends AbstractEntityValidator<A, T> {
  /** Message parameter holding the entity field name */
  public static final String MESSAGE_PARAMETER_FIELD = "field";

  /** Annotation method to get validated entity field name */
  public static final String METHOD_FIELD = "fieldName";

  /** Annotation method signature to get validated entity field name */
  private static final String METHOD_FIELD_SIGNATURE = "String " + METHOD_FIELD + "()";

  /** The entity field name that this validatos can validate */
  private String fieldName;

  @Override
  public void initialize(A constraintAnnotation) {
    super.initialize(constraintAnnotation);
    Class<? extends Annotation> currentClass = constraintAnnotation.getClass();
    Method fieldMethod;
    try {
      fieldMethod = currentClass.getMethod(METHOD_FIELD);
      fieldName = (String) fieldMethod.invoke(constraintAnnotation);
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      throw new IllegalArgumentException(
          String.format("Annotation %s does not have method %s", currentClass.getSimpleName(), METHOD_FIELD_SIGNATURE),
          e);
    }
  }

  /**
   * The entity field name that this validatos can validate.
   * 
   * @return the entity field name
   */
  protected String getFieldName() {
    return fieldName;
  }

  /**
   * Gets the field value to be validated from the provided entity.
   * 
   * @param value entity
   * @return the field value
   */
  protected Object getFieldValue(Object value) {
    BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
    return wrapper.getPropertyValue(getFieldName());
  }

  /**
   * Customize the validation logic for the specific entity field level
   * {@link ConstraintValidator}. The state of {@code value} must not be altered.
   * 
   * @param value   object to validate
   * @param context context in which the constraint is evaluated
   *
   * @return {@code false} if {@code value} does not pass the constraint
   */
  protected abstract boolean validate(Object value, ConstraintValidatorContext context);

  /**
   * Add "entity", "field" and "value" message parameters so they can be used in
   * the error message.
   * <p>
   * The value can be customized with the
   * {@link #getValue(Object, ConstraintValidatorContext)} method.
   * 
   * @param value   object to validate
   * @param context context in which the constraint is evaluated
   */
  @Override
  protected void addEntityMessageParameters(Object value, ConstraintValidatorContext context) {
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    // Add "entity", "field" and "value" message parameters so they can be used in
    // the error message
    super.addEntityMessageParameters(value, context);
    hibernateContext.addMessageParameter(MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(getFieldName()));
  }
}
