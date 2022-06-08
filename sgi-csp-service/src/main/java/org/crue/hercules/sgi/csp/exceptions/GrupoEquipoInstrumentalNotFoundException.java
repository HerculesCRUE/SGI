package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.GrupoEquipoInstrumental;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class GrupoEquipoInstrumentalNotFoundException extends CspNotFoundException {

  /**
   * GrupoEquipoInstrumentalNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public GrupoEquipoInstrumentalNotFoundException(Long id) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(GrupoEquipoInstrumental.class))
        .parameter("id", id).build());
  }
}
