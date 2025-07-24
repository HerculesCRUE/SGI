package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class SolicitudProyectoClasificacionNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_SOLICITUD_PROYECTO_CLASIFICACION = "org.crue.hercules.sgi.csp.model.SolicitudProyectoClasificacion.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoClasificacionNotFoundException(Long solicitudProyectoClasificacionId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_SOLICITUD_PROYECTO_CLASIFICACION),
            solicitudProyectoClasificacionId }));
  }
}
