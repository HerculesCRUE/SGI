package org.crue.hercules.sgi.pii.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.ProcedimientoDocumento;

public class ProcedimientoDocumentoNotFoundException extends PiiNotFoundException {
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ProcedimientoDocumentoNotFoundException(Long procedimientoDocumentoId) {
    super(ProblemMessage.builder().key(PiiNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(ProcedimientoDocumento.class))
        .parameter("id", procedimientoDocumentoId).build());
  }
}
