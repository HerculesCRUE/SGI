package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * TipoActividadNotFoundException
 */
public class TipoActividadNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_TIPO_ACTIVIDAD = "org.crue.hercules.sgi.eti.model.TipoActividad.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public TipoActividadNotFoundException(Long tipoActividadId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_ACTIVIDAD), tipoActividadId }));
  }

}