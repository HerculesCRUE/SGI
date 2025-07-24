package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * EstadoMemoriaNotFoundException
 */
public class EstadoMemoriaNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_ESTADO_MEMORIA = "org.crue.hercules.sgi.eti.model.EstadoMemoria.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public EstadoMemoriaNotFoundException(Long estadoMemoriaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_ESTADO_MEMORIA), estadoMemoriaId }));
  }

}