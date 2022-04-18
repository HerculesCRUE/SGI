package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * CspNotFoundException
 */
public class CspNotFoundException extends NotFoundException {

  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String MESSAGE_KEY_ID = "id";

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public CspNotFoundException(String message) {
    super(message);
  }

  public CspNotFoundException(Long id, Class<?> clazz) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(clazz))
        .parameter(MESSAGE_KEY_ID, id).build());
  }

}
