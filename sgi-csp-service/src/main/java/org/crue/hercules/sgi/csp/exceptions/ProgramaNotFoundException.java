package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ProgramaNotFoundException
 */
public class ProgramaNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROGRAMA = "org.crue.hercules.sgi.csp.model.Programa.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProgramaNotFoundException(Long programaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROGRAMA), programaId }));
  }
}
