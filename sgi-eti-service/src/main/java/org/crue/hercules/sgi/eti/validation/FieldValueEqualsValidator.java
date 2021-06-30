package org.crue.hercules.sgi.eti.validation;

import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class FieldValueEqualsValidator implements ConstraintValidator<FieldValueEquals, Object> {
  private PersistenceUnitUtil persistenceUnitUtil;
  private JpaRepository<Object, Object> repository;
  private Class<?> entityClass;
  private String field;
  private String value;

  @Override
  @SuppressWarnings("unchecked")
  public void initialize(FieldValueEquals constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    entityClass = constraintAnnotation.entityClass();
    field = constraintAnnotation.field();
    value = constraintAnnotation.value();
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
    if (value != null) {
      addEntityMessageParameter(context);
      // type check
      if (!entityClass.isInstance(value)) {
        throw new IllegalArgumentException("The provided value is not " + entityClass.getSimpleName());
      }
      Object id = persistenceUnitUtil.getIdentifier(value);
      if (id != null) {
        Optional<Object> entity = repository.findById(id);

        if (entity.isPresent()) {
          BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(entity.get());
          Object fieldValue = wrapper.getPropertyValue(field);
          if (this.value.equals(fieldValue)) {
            return true;
          }
        }
      }
    }
    addEntityMessageParameter(context);

    return false;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter so it can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(entityClass));
    // Add "entity" message parameter so it can be used in the error message
    hibernateContext.addMessageParameter("field", ApplicationContextSupport.getMessage(field));
  }
}
