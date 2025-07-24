package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * TipoEstadoMemoriaNotFoundException
 */
public class TipoEstadoMemoriaNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_TIPO_ESTADO_MEMORIA = "org.crue.hercules.sgi.eti.model.TipoEstadoMemoria.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public TipoEstadoMemoriaNotFoundException(Long tipoEstadoMemoriaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_ESTADO_MEMORIA), tipoEstadoMemoriaId }));
  }

}