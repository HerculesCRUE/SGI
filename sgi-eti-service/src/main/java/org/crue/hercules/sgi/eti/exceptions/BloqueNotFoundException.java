package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * BloqueNotFoundException
 */
public class BloqueNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_BLOQUE = "org.crue.hercules.sgi.eti.model.Bloque.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public BloqueNotFoundException(Long bloqueId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_BLOQUE), bloqueId }));
  }

}