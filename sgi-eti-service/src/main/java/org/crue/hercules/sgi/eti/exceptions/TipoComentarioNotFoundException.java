package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * TipoComentarioNotFoundException
 */
public class TipoComentarioNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_TIPO_COMENTARIO = "org.crue.hercules.sgi.eti.model.TipoComentario.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public TipoComentarioNotFoundException(Long tipoComentarioId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_COMENTARIO), tipoComentarioId }));
  }

}