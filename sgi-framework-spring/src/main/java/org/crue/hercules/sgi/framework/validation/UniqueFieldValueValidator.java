package org.crue.hercules.sgi.framework.validation;

import javax.persistence.Query;
import javax.validation.ConstraintValidatorContext;

/**
 * Field validator to check that an entity current field value is not already
 * present in the database.
 * 
 */
public class UniqueFieldValueValidator extends AbstractEntityFieldValidator<UniqueFieldValue, Object> {
  private static final String QUERY_COUNT = "SELECT count(e) FROM %s e WHERE %s=?1";
  private Object fieldValue;

  @Override
  protected boolean validate(Object value, ConstraintValidatorContext context) {
    Class<?> entityClass = getEntityClass();
    String fieldName = getFieldName();
    Query query = entityManager.createQuery(String.format(QUERY_COUNT, entityClass.getSimpleName(), fieldName));
    fieldValue = getFieldValue(value);
    if (fieldValue == null) {
      return false;
    }
    Long count = (Long) query.setParameter(1, fieldValue).getSingleResult();
    return count.equals(0l);
  }

  @Override
  protected Object getValue(Object value, ConstraintValidatorContext context) {
    return fieldValue;
  }
}
