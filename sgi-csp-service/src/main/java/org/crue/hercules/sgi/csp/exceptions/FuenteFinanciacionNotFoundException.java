package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * FuenteFinanciacionNotFoundException
 */
public class FuenteFinanciacionNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public FuenteFinanciacionNotFoundException(Long fuenteFinanciacionId) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(FuenteFinanciacion.class))
        .parameter("id", fuenteFinanciacionId).build());
  }
}
