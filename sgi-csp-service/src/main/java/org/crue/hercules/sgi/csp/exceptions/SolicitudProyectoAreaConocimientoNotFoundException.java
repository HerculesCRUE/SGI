package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class SolicitudProyectoAreaConocimientoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_SOLICITUD_PROYECTO_AREA_CONOCIMIENTO = "org.crue.hercules.sgi.csp.model.SolicitudProyectoAreaConocimiento.message";
  /**
   * Excepcion para Area conocimiento no encontrada
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoAreaConocimientoNotFoundException(Long solicitudProyectoAreaConocimientoId) {

    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_SOLICITUD_PROYECTO_AREA_CONOCIMIENTO),
            solicitudProyectoAreaConocimientoId }));
  }
}
