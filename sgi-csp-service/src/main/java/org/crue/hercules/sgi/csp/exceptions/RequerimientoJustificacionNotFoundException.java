package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * RequerimientoJustificacionNotFoundException
 */
public class RequerimientoJustificacionNotFoundException extends CspNotFoundException {

  private static final long serialVersionUID = 1L;

  public RequerimientoJustificacionNotFoundException(Long requerimientoJustificacionId) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(RequerimientoJustificacion.class))
        .parameter("id", requerimientoJustificacionId).build());
  }
}
