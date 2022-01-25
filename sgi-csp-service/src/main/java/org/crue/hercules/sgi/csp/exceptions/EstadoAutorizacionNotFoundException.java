package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.EstadoAutorizacion;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class EstadoAutorizacionNotFoundException extends CspNotFoundException {

  /**
   * EstadoAutorizacionNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public EstadoAutorizacionNotFoundException(Long estadoAutorizacionId) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(
            EstadoAutorizacion.class))
        .parameter("id", estadoAutorizacionId).build());
  }
}
