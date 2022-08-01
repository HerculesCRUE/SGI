package org.crue.hercules.sgi.eer.exceptions;

import org.crue.hercules.sgi.eer.model.EmpresaDocumento;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class EmpresaDocumentoNotFoundException extends EerNotFoundException {

  /**
   * EmpresaDocumentoNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public EmpresaDocumentoNotFoundException(Long id) {
    super(ProblemMessage.builder().key(EerNotFoundException.class)
        .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(EmpresaDocumento.class))
        .parameter(MESSAGE_KEY_ID, id).build());
  }
}
