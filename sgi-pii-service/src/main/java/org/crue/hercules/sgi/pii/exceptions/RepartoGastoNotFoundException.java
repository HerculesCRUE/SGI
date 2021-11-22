package org.crue.hercules.sgi.pii.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.RepartoGasto;

public class RepartoGastoNotFoundException extends PiiNotFoundException {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public RepartoGastoNotFoundException(Long repartoGastoId) {
    super(ProblemMessage.builder().key(PiiNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(RepartoGasto.class)).parameter("id", repartoGastoId)
        .build());
  }
}
