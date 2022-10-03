package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.GastoRequerimientoJustificacion;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class GastoRequerimientoJustificacionNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public GastoRequerimientoJustificacionNotFoundException(Long gastoRequerimientoJustificacionId) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(GastoRequerimientoJustificacion.class))
        .parameter("id", gastoRequerimientoJustificacionId).build());
  }
}
