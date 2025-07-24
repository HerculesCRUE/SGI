package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * SolicitudProyectoSocioEquipoNotFoundException
 */
public class SolicitudProyectoSocioEquipoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_SOLICITUD_PROYECTO_SOCIO_EQUIPO = "org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipoSocio.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoSocioEquipoNotFoundException(Long solicitudProyectoEquipoSocioId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_SOLICITUD_PROYECTO_SOCIO_EQUIPO),
            solicitudProyectoEquipoSocioId }));
  }

}
