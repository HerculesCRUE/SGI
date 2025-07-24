package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ApartadoNotFoundException
 */
public class ApartadoNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_APARTADO = "org.crue.hercules.sgi.eti.model.Apartado.message";

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ApartadoNotFoundException(Long apartadoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_APARTADO), apartadoId }));
  }

}