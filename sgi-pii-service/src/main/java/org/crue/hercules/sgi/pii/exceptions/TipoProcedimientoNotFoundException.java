package org.crue.hercules.sgi.pii.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.TipoProcedimiento;

public class TipoProcedimientoNotFoundException extends PiiNotFoundException {
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public TipoProcedimientoNotFoundException(Long tipoProcedimientoId) {
    super(ProblemMessage.builder().key(PiiNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(TipoProcedimiento.class))
        .parameter("id", tipoProcedimientoId).build());
  }
}
