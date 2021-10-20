package org.crue.hercules.sgi.rel.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rel.model.Relacion;

public class RelacionNotFoundException extends RelNotFoundException {
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public RelacionNotFoundException(Long relacionId) {
    super(ProblemMessage.builder().key(RelNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(Relacion.class)).parameter("id", relacionId).build());
  }
}
