package org.crue.hercules.sgi.eer.exceptions;

import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class EmpresaNotFoundException extends EerNotFoundException {

  /**
   * EmpresaNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public EmpresaNotFoundException(Long id) {
    super(ProblemMessage.builder().key(EerNotFoundException.class)
        .parameter(
            PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Empresa.class))
        .parameter(MESSAGE_KEY_ID, id).build());
  }

}
