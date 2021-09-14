package org.crue.hercules.sgi.pii.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.InvencionGasto;

public class InvencionGastoNotFoundException extends PiiNotFoundException {
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public InvencionGastoNotFoundException(Long invencionGastoId) {
    super(ProblemMessage.builder().key(PiiNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(InvencionGasto.class))
        .parameter("id", invencionGastoId).build());
  }
}
