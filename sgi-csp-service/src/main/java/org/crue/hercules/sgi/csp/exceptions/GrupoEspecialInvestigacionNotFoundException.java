package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.GrupoEspecialInvestigacion;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class GrupoEspecialInvestigacionNotFoundException extends CspNotFoundException {

  /**
   * GrupoEspecialInvestigacionNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public GrupoEspecialInvestigacionNotFoundException(Long id) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(GrupoEspecialInvestigacion.class))
        .parameter("id", id).build());
  }

}
