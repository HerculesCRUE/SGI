package org.crue.hercules.sgi.framework.validation;

import javax.persistence.PersistenceUnitUtil;
import javax.validation.ConstraintValidatorContext;

/**
 * Checks that the database has an entity with the identification of the
 * provided entity.
 */
public class ExistsByIdValidator extends AbstractEntityValidator<ExistsById, Object> {
  private PersistenceUnitUtil persistenceUnitUtil;
  private Object id;

  @Override
  public void initialize(ExistsById constraintAnnotation) {
    super.initialize(constraintAnnotation);
    persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
  }

  @Override
  protected boolean validate(Object value, ConstraintValidatorContext context) {
    Class<?> entityClass = getEntityClass();
    id = persistenceUnitUtil.getIdentifier(value);
    if (id != null) {
      Object entity = entityManager.find(entityClass, id);
      return (entity != null);
    } else {
      throw new IllegalArgumentException(
          String.format("Can't get identifier value from type %s", entityClass.getSimpleName()));
    }
  }

  @Override
  protected Object getValue(Object value, ConstraintValidatorContext context) {
    return id;
  }
}
