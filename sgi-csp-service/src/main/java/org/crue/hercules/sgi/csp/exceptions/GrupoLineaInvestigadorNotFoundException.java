package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigador;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class GrupoLineaInvestigadorNotFoundException extends CspNotFoundException {

  /**
   * GrupoLineaInvestigadorNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public GrupoLineaInvestigadorNotFoundException(Long id) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(GrupoLineaInvestigador.class))
        .parameter("id", id).build());
  }
}
