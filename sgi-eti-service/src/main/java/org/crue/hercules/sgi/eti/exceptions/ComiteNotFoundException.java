package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ComiteNotFoundException
 */
public class ComiteNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_COMITE = "org.crue.hercules.sgi.eti.model.Comite.message";
  /**
   * Serial version.
   */
  private static final long serialVersionUID = 1L;

  public ComiteNotFoundException(Long comiteId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_COMITE), comiteId }));
  }
}