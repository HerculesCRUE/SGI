package org.crue.hercules.sgi.prc.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.crue.hercules.sgi.prc.validation.UniqueElementsByFields.List;

@Constraint(validatedBy = UniqueElementsByFieldsValidator.class)
@Documented
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Repeatable(List.class)
public @interface UniqueElementsByFields {
  String message() default "{org.hibernate.validator.constraints.UniqueElements.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  /**
   * The entity fields names whose value must be unique in the object.
   * 
   * @return the field name
   */
  String[] fieldsNames();

  /**
   * Defines several {@code @UniqueElementsByFields} annotations on the same
   * element.
   */
  @Target({ TYPE, FIELD })
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface List {
    UniqueElementsByFields[] value();
  }
}
