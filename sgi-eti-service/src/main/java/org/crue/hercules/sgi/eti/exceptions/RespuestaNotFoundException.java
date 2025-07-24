package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * RespuestaNotFoundException
 */
public class RespuestaNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_RESPUESTA = "org.crue.hercules.sgi.eti.model.Respuesta.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public RespuestaNotFoundException(Long respuestaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_RESPUESTA), respuestaId }));
  }

}