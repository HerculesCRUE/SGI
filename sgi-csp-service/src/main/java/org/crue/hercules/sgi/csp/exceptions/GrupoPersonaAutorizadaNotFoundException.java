package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.GrupoPersonaAutorizada;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class GrupoPersonaAutorizadaNotFoundException extends CspNotFoundException {

  /**
   * GrupoPersonaAutorizadaNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public GrupoPersonaAutorizadaNotFoundException(Long id) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(GrupoPersonaAutorizada.class))
        .parameter("id", id).build());
  }
}
