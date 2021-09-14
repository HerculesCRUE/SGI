package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.GastoProyecto;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * GastoProyectoNotFoundException
 */
public class GastoProyectoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public GastoProyectoNotFoundException(Long gastoProyectoId) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(GastoProyecto.class)).parameter("id", gastoProyectoId)
        .build());
  }
}
