package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * MemoriaNotFoundException
 */
public class MemoriaNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_MEMORIA = "org.crue.hercules.sgi.eti.model.Memoria.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public MemoriaNotFoundException(Long memoriaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_MEMORIA), memoriaId }));
  }

}