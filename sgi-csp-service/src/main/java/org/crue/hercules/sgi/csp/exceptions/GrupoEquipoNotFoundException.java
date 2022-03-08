package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class GrupoEquipoNotFoundException extends CspNotFoundException {

  /**
   * GrupoEquipoNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public GrupoEquipoNotFoundException(Long id) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(GrupoEquipo.class))
        .parameter("id", id).build());
  }
}
