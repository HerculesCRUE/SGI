package org.crue.hercules.sgi.rel.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.rel.model.Identifiable;
import org.crue.hercules.sgi.rel.repository.ActivableRepository;
import org.crue.hercules.sgi.framework.data.jpa.domain.Activable;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class EntidadActivaValidator implements ConstraintValidator<EntidadActiva, Object> {
  private ActivableRepository repository;
  private Class<?> entityClass;

  public EntidadActivaValidator() throws BeansException {
  }

  @Override
  public void initialize(EntidadActiva constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    entityClass = constraintAnnotation.entityClass();
    // type check
    if (!(Activable.class.isAssignableFrom(entityClass)) || !(Identifiable.class.isAssignableFrom(entityClass))) {
      throw new IllegalArgumentException(
          "The provided entityClass is not " + Activable.class.getName() + " or " + Identifiable.class.getName());
    }
    Class<?> repositoryClass = constraintAnnotation.repositoryClass();
    if (repositoryClass == void.class) {
      try {
        repositoryClass = Class
            .forName("org.crue.hercules.sgi.rel.repository.".concat(entityClass.getSimpleName()).concat("Repository"));
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    // type check
    if (!ActivableRepository.class.isAssignableFrom(repositoryClass)) {
      throw new IllegalArgumentException(
          "The repositoy class " + repositoryClass.getName() + " is not " + ActivableRepository.class.getName());
    }
    ApplicationContext applicationContext = ApplicationContextSupport.getApplicationContext();
    repository = (ActivableRepository) applicationContext.getBean(repositoryClass);
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
    Identifiable identifiable = (Identifiable) value;
    // database check
    boolean returnValue = identifiable.getId() != null && repository.existsByIdAndActivoIsTrue(identifiable.getId());
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
