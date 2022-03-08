package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.GrupoTipo;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class GrupoTipoNotFoundException extends CspNotFoundException {

  /**
   * GrupoTipoNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public GrupoTipoNotFoundException(Long id) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(GrupoTipo.class))
        .parameter("id", id).build());
  }

}
