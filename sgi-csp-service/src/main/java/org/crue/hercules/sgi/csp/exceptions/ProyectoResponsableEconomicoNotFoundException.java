package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ProyectoResponsableEconomicoNotFoundException
 */
public class ProyectoResponsableEconomicoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoResponsableEconomicoNotFoundException(Long proyectoResponsableEconomicoId) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(ProyectoResponsableEconomico.class))
        .parameter("id", proyectoResponsableEconomicoId).build());
  }
}
