package org.crue.hercules.sgi.pii.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.RepartoEquipoInventor;

public class RepartoEquipoInventorNotFoundException extends PiiNotFoundException {
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public RepartoEquipoInventorNotFoundException(Long repartoEquipoInventorId) {
    super(ProblemMessage.builder().key(PiiNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(RepartoEquipoInventor.class))
        .parameter("id", repartoEquipoInventorId).build());
  }
}
