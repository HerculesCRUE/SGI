package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ProyectoClasificacionNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_CLASIFICACION = "org.crue.hercules.sgi.csp.model.ProyectoClasificacion.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoClasificacionNotFoundException(Long proyectoClasificacionId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_CLASIFICACION),
            proyectoClasificacionId }));
  }
}
