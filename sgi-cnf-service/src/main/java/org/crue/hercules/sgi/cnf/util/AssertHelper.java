package org.crue.hercules.sgi.cnf.util;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.util.Assert;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssertHelper {
  public static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  public static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  public static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  public static final String MESSAGE_KEY_NAME = "name";
  public static final String MESSAGE_KEY_VALUE = "value";

  /**
   * Comprueba que el campo no sea null
   * 
   * @param fieldValue      el valor del campo.
   * @param clazz           clase para la que se comprueba el campo.
   * @param messageKeyField clave del campo del messages properties.
   */
  public static void fieldNotNull(Object fieldValue, Class<?> clazz, String messageKeyField) {
    Assert.notNull(fieldValue,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(messageKeyField))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(clazz))
            .build());
  }

}
