package org.crue.hercules.sgi.prc.validation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

/**
 * Validates that the provided collection of objects only contains unique
 * elements, i.e.
 * that we can't find 2 equal elements in the collection.
 * 
 * 
 * Uniqueness is defined by the {@code equals()} method of the objects being
 * compared and fieldName property from object
 *
 */
@SuppressWarnings("rawtypes")
// as per the JLS, Collection is a subtype of Collection, so we need to
// explicitly reference Collection here to support having properties defined as
// Collection (see HV-1551)
public class UniqueElementsByFieldsValidator implements ConstraintValidator<UniqueElementsByFields, Collection> {

  /** The entity field name that this validatos can validate */
  private String[] fieldsNames;

  private Object getFieldValue(Object value, String fieldName) {
    BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);
    return wrapper.getPropertyValue(fieldName);
  }

  private String getFieldValueToString(Object value, String fieldName) {
    Object objectValue = getFieldValue(value, fieldName);
    return null != objectValue ? objectValue.toString() : "";
  }

  @Override
  public void initialize(UniqueElementsByFields uniqueElementsByField) {
    this.fieldsNames = uniqueElementsByField.fieldsNames();
  }

  /**
   * @param collection                 the collection to validate
   * @param constraintValidatorContext context in which the constraint is
   *                                   evaluated
   *
   * @return true if the input collection is null or does not contain duplicate
   *         elements
   */
  @Override
  public boolean isValid(Collection collection, ConstraintValidatorContext constraintValidatorContext) {
    if (collection == null || collection.size() < 2) {
      return true;
    }

    Map<Integer, Object> duplicates = findDuplicatesAndPosition(collection);

    if (duplicates.isEmpty()) {
      return true;
    }

    if (constraintValidatorContext instanceof HibernateConstraintValidatorContext) {

      String duplicatesParameter = duplicates.entrySet().stream()
          .map(e -> String.format("%s [%s]", getBasePath(constraintValidatorContext), String.valueOf(e.getKey())))
          .collect(Collectors.joining(", "));
      List<Object> dynamicPayload = CollectionHelper
          .toImmutableList(CollectionHelper.newArrayList(duplicates.values()));
      constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class)
          .addMessageParameter("duplicates", duplicatesParameter)
          .withDynamicPayload(dynamicPayload);
    }

    return false;
  }

  private String getBasePath(ConstraintValidatorContext constraintValidatorContext) {
    ConstraintValidatorContextImpl context = (ConstraintValidatorContextImpl) constraintValidatorContext;
    return context.getConstraintViolationCreationContexts().get(0).getPath().asString();
  }

  private Map<Integer, Object> findDuplicatesAndPosition(Collection<?> collection) {
    Set<Object> uniqueElements = CollectionHelper.newHashSet(collection.size());
    Set<String> uniqueFieldsElements = CollectionHelper.newHashSet(collection.size());

    return IntStream.range(0, collection.size())
        .filter(i -> {
          Object objectToCompare = collection.toArray()[i];
          boolean isDuplicate = !uniqueElements.add(objectToCompare);

          if (!isDuplicate) {
            String fieldValueKey = getFieldsValuesKey(objectToCompare);
            if (fieldValueKey.length() > 1) {
              isDuplicate = !uniqueFieldsElements.add(fieldValueKey);
            }
          }
          return isDuplicate;
        })
        .boxed()
        .collect(Collectors.toMap(Function.identity(), i -> collection.toArray()[i]));
  }

  private String getFieldsValuesKey(Object value) {
    String fieldValueKey = "";
    if (null != fieldsNames) {
      fieldValueKey = Stream.of(fieldsNames).map(fieldName -> getFieldValueToString(value, fieldName))
          .collect(Collectors.joining("#"));
    }
    return fieldValueKey;
  }

}
