package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ComentarioNotFoundException
 */
public class ComentarioNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_COMENTARIO = "org.crue.hercules.sgi.eti.model.Comentario.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ComentarioNotFoundException(Long comentarioId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_COMENTARIO), comentarioId }));
  }

}