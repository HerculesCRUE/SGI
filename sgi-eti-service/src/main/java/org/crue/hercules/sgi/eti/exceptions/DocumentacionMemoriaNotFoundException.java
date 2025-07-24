package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * DocumentacionMemoriaNotFoundException
 */
public class DocumentacionMemoriaNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_DOCUMENTACION_MEMORIA = "org.crue.hercules.sgi.eti.model.DocumentacionMemoria.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public DocumentacionMemoriaNotFoundException(Long documentacionMemoriaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(
            MSG_MODEL_DOCUMENTACION_MEMORIA), documentacionMemoriaId }));
  }

}