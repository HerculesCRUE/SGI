package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.ProyectoAgrupacionGasto;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ProyectoAgrupacionGastoNotFoundException
 */
public class ProyectoAgrupacionGastoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoAgrupacionGastoNotFoundException(Long proyectoAgrupacionGastoId) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(ProyectoAgrupacionGasto.class))
        .parameter("id", proyectoAgrupacionGastoId).build());
  }
}
