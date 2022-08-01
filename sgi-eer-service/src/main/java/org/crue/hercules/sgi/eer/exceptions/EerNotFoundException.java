package org.crue.hercules.sgi.eer.exceptions;

import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * EerNotFoundException
 */
public class EerNotFoundException extends NotFoundException {

  protected static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  protected static final String MESSAGE_KEY_ID = "id";

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public EerNotFoundException(String message) {
    super(message);
  }

  public EerNotFoundException(Long id, Class<?> clazz) {
    super(ProblemMessage.builder().key(EerNotFoundException.class)
        .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(clazz))
        .parameter(MESSAGE_KEY_ID, id).build());
  }

}
