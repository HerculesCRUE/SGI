package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * EstadoActaNotFoundException
 */
public class EstadoActaNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_ESTADO_ACTA = "org.crue.hercules.sgi.eti.model.EstadoActa.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public EstadoActaNotFoundException(Long estadoActaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_ESTADO_ACTA), estadoActaId }));
  }

}