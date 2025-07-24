package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * TareaNotFoundException
 */
public class TareaNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_TAREA = "org.crue.hercules.sgi.eti.model.Tarea.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public TareaNotFoundException(Long tareaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_TAREA), tareaId }));
  }

}