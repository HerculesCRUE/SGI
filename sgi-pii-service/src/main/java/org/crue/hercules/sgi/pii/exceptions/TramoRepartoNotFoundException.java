package org.crue.hercules.sgi.pii.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.TramoReparto;

public class TramoRepartoNotFoundException extends PiiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public TramoRepartoNotFoundException(Long tramoRepartoId) {
    super(ProblemMessage.builder().key(PiiNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(TramoReparto.class)).parameter("id", tramoRepartoId)
        .build());
  }
}
