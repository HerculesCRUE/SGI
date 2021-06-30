package org.crue.hercules.sgi.eti.validation;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class ExistsByIdValidator implements ConstraintValidator<ExistsById, Object> {
  private PersistenceUnitUtil persistenceUnitUtil;
  private JpaRepository<Object, Object> repository;
  private Class<?> entityClass;

  @Override
  @SuppressWarnings("unchecked")
  public void initialize(ExistsById constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    entityClass = constraintAnnotation.entityClass();
    // type check
    if (!(entityClass.isAnnotationPresent(Entity.class))) {
      throw new IllegalArgumentException("The provided entityClass is not annotated with @Entity");
    }
    Class<?> repositoryClass = constraintAnnotation.repositoryClass();
    if (repositoryClass == void.class) {
      try {
        repositoryClass = Class
            .forName("org.crue.hercules.sgi.eti.repository.".concat(entityClass.getSimpleName()).concat("Repository"));
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    ApplicationContext applicationContext = ApplicationContextSupport.getApplicationContext();
    repository = (JpaRepository<Object, Object>) applicationContext.getBean(repositoryClass);
    EntityManager em = (EntityManager) applicationContext.getBean(EntityManager.class);
    persistenceUnitUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    // fast-return
    if (value == null) {
      addEntityMessageParameter(context);
      return false;
    }
    // type check
    if (!entityClass.isInstance(value)) {
      throw new IllegalArgumentException("The provided value is not " + entityClass.getSimpleName());
    }
    Object id = persistenceUnitUtil.getIdentifier(value);
    // database check
    boolean returnValue = id != null && repository.existsById(id);
    if (!returnValue) {
      addEntityMessageParameter(context);
    }

    return returnValue;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(entityClass));
  }
}
