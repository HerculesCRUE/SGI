package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class GrupoLineaInvestigacionNotFoundException extends CspNotFoundException {

  /**
   * GrupoLineaInvestigacionNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public GrupoLineaInvestigacionNotFoundException(Long id) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(GrupoLineaInvestigacion.class))
        .parameter("id", id).build());
  }
}
