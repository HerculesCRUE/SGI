package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ActaNotFoundException
 */
public class ActaNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_ACTA = "org.crue.hercules.sgi.eti.model.Acta.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ActaNotFoundException(Long actaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_ACTA), actaId }));
  }

}