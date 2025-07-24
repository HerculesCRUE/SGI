package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * TipoMemoriaNotFoundException
 */
public class TipoMemoriaNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_TIPO_MEMORIA = "org.crue.hercules.sgi.eti.model.TipoMemoria.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public TipoMemoriaNotFoundException(Long tipoMemoriaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_MEMORIA), tipoMemoriaId }));
  }

}