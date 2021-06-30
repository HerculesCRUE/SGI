package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoResponsableEconomico;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * SolicitudProyectoResponsableEconomicoNotFoundException
 */
public class SolicitudProyectoResponsableEconomicoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoResponsableEconomicoNotFoundException(Long solicitudProyectoResponsableEconomicoId) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(SolicitudProyectoResponsableEconomico.class))
        .parameter("id", solicitudProyectoResponsableEconomicoId).build());
  }
}
