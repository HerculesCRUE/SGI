package org.crue.hercules.sgi.prc.util;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.util.Assert;

public class AssertHelper {
  private static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  private static final String PROBLEM_MESSAGE_ISNULL = "isNull";
  private static final String MESSAGE_KEY_ID = "id";

  private AssertHelper() {
  }

  /**
   * Comprueba que el id no sea null
   * 
   * @param id    Id de la entidad
   * @param clazz clase para la que se comprueba el id
   */
  public static void idIsNull(Long id, Class<?> clazz) {
    Assert.isNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(clazz))
            .build());
  }

  /**
   * Comprueba que el id no sea null
   * 
   * @param id    Id de la entidad
   * @param clazz clase para la que se comprueba el id
   */
  public static void idNotNull(Long id, Class<?> clazz) {
    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(clazz))
            .build());
  }

}
