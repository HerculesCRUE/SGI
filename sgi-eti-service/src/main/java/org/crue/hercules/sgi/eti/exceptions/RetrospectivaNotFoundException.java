package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * RetrospectivaNotFoundException
 */
public class RetrospectivaNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_RETROSPECTIVA = "org.crue.hercules.sgi.eti.model.Retrospectiva.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public RetrospectivaNotFoundException(Long retrospectivaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_RETROSPECTIVA), retrospectivaId }));
  }

}