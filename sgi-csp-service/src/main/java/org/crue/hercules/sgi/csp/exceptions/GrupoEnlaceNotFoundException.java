package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.GrupoEnlace;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class GrupoEnlaceNotFoundException extends CspNotFoundException {

  /**
   * GrupoEnlaceNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public GrupoEnlaceNotFoundException(Long id) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(GrupoEnlace.class))
        .parameter("id", id).build());
  }
}
