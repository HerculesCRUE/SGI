package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * SolicitudProyectoNotFoundException
 */
public class SolicitudProyectoEquipoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_SOLICITUD_PROYECTO_EQUIPO = "org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoEquipoNotFoundException(Long solicitudProyectoEquipoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_SOLICITUD_PROYECTO_EQUIPO),
            solicitudProyectoEquipoId }));
  }

}
