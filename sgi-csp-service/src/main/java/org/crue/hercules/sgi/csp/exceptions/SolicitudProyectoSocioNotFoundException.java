package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * SolicitudProyectoSocioNotFoundException
 */
public class SolicitudProyectoSocioNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_SOLICITUD_PROYECTO_SOCIO = "org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoSocioNotFoundException(Long solicitudProyectoSocioId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_SOLICITUD_PROYECTO_SOCIO),
            solicitudProyectoSocioId }));
  }

}
