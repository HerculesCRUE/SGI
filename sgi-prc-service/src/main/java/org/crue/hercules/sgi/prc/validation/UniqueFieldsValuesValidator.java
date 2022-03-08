package org.crue.hercules.sgi.prc.validation;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Query;
import javax.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

/**
 * Field validator to check that an entity current fields values is not already
 * present in the database.
 * 
 */
@Slf4j
public class UniqueFieldsValuesValidator extends AbstractEntityFieldsValidator<UniqueFieldsValues, Object> {
  private static final String QUERY_COUNT = "SELECT count(e) FROM %s e WHERE ";
  private Object fieldValue;

  @Override
  protected boolean validate(Object value, ConstraintValidatorContext context) {
    boolean result = true;

    try {
      Class<?> entityClass = getEntityClass();
      String[] fieldsNames = getFieldsNames();

      fieldValue = Stream.of(fieldsNames)
          .map(fieldName -> getFieldValue(value, fieldName).toString())
          .collect(Collectors.joining(", "));

      AtomicInteger fieldIndex = new AtomicInteger();
      String queryCountFilters = Stream.of(fieldsNames)
          .map(fieldName -> {
            Object fieldValueByName = getFieldValue(value, fieldName);
            if (!fieldValueByName.equals(NULL_VALUE_DEFAULT)) {
              fieldName += "=?" + (fieldIndex.getAndIncrement() + 1);
            } else {
              fieldName += " is null";
            }
            return fieldName;
          })
          .collect(Collectors.joining(" AND "));

      Query query = entityManager
          .createQuery(String.format(QUERY_COUNT + "%s", entityClass.getSimpleName(), queryCountFilters));

      AtomicInteger parameterIndex = new AtomicInteger();
      Stream.of(fieldsNames)
          .forEach(fieldName -> {
            Object fieldValueByName = getFieldValue(value, fieldName);
            if (!fieldValueByName.equals(NULL_VALUE_DEFAULT)) {
              query.setParameter(parameterIndex.getAndIncrement() + 1, getFieldValue(value, fieldName));
            }
          });

      Long count = (Long) query.getSingleResult();

      result = count.equals(0l);

    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return result;
  }

  @Override
  protected Object getValue(Object value, ConstraintValidatorContext context) {
    return fieldValue;
  }
}