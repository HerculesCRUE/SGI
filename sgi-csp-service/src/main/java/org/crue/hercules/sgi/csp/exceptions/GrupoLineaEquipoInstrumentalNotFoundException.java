package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.GrupoLineaEquipoInstrumental;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class GrupoLineaEquipoInstrumentalNotFoundException extends CspNotFoundException {

  /**
   * GrupoLineaEquipoInstrumentalNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public GrupoLineaEquipoInstrumentalNotFoundException(Long id) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(GrupoLineaEquipoInstrumental.class))
        .parameter("id", id).build());
  }
}
