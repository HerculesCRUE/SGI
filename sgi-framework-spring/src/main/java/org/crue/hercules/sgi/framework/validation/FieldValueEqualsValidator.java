package org.crue.hercules.sgi.framework.validation;

import javax.persistence.PersistenceUnitUtil;
import javax.validation.ConstraintValidatorContext;

/**
 * Field validator to check than an entity database field value is equal to the
 * provided value.
 * <p>
 * The entity is read from database using it's identifier before checking it's
 * field value.
 */
public class FieldValueEqualsValidator extends AbstractEntityFieldValidator<FieldValueEquals, Object> {
  private PersistenceUnitUtil persistenceUnitUtil;
  private String value;

  @Override
  public void initialize(FieldValueEquals constraintAnnotation) {
    super.initialize(constraintAnnotation);
    value = constraintAnnotation.value();
    persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
  }

  @Override
  protected boolean validate(Object value, ConstraintValidatorContext context) {
    Class<?> entityClass = getEntityClass();
    Object id = persistenceUnitUtil.getIdentifier(value);
    if (id != null) {
      Object entity = entityManager.find(getEntityClass(), id);
      if (entity != null) {
        Object fieldValue = getFieldValue(entity);
        if (this.value.equals(fieldValue)) {
          return true;
        }
      }
    } else {
      throw new IllegalArgumentException(
          String.format("Can't get identifier value from type %s", entityClass.getSimpleName()));
    }
    return false;
  }

  @Override
  protected Object getValue(Object value, ConstraintValidatorContext context) {
    return this.value;
  }
}
