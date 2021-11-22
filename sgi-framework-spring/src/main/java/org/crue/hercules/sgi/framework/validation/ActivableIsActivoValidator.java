package org.crue.hercules.sgi.framework.validation;

import javax.persistence.PersistenceUnitUtil;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.data.jpa.domain.Activable;

/**
 * Checks that the database has an {@link Activable} entity with the identifier
 * of the provided entity and the field "activo" TRUE.
 */
public class ActivableIsActivoValidator extends AbstractEntityValidator<ActivableIsActivo, Object> {
  private PersistenceUnitUtil persistenceUnitUtil;
  private Object id;

  @Override
  public void initialize(ActivableIsActivo constraintAnnotation) {
    super.initialize(constraintAnnotation);
    Class<?> entityClass = getEntityClass();
    // type check
    if (!(Activable.class.isAssignableFrom(entityClass))) {
      throw new IllegalArgumentException("The provided entityClass is not " + Activable.class.getSimpleName());
    }
    persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
  }

  @Override
  public boolean validate(Object value, ConstraintValidatorContext context) {
    Class<?> entityClass = getEntityClass();
    id = persistenceUnitUtil.getIdentifier(value);
    if (id != null) {
      Activable entity = (Activable) entityManager.find(entityClass, id);
      if (entity != null && Boolean.TRUE.equals(entity.getActivo())) {
        return true;
      }
    } else {
      throw new IllegalArgumentException(
          String.format("Can't get identifier value from type %s", entityClass.getSimpleName()));
    }
    return false;
  }

  @Override
  protected Object getValue(Object value, ConstraintValidatorContext context) {
    return id;
  }
}
