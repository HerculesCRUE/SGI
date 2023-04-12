package org.crue.hercules.sgi.cnf.exceptions;

import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * CnfNotFoundException
 */
public class CnfNotFoundException extends NotFoundException {

  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String MESSAGE_KEY_NAME = "name";

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public CnfNotFoundException(String message) {
    super(message);
  }

  public CnfNotFoundException(String name, Class<?> clazz) {
    super(ProblemMessage.builder().key(CnfNotFoundException.class)
        .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(clazz))
        .parameter(MESSAGE_KEY_NAME, name).build());
  }

}
