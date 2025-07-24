package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * TipoTareaNotFoundException
 */
public class TipoTareaNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_TIPO_TAREA = "org.crue.hercules.sgi.eti.model.TipoTarea.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public TipoTareaNotFoundException(Long tipoTareaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_TAREA), tipoTareaId }));
  }

}