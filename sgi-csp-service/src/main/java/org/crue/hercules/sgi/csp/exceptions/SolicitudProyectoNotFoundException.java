package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * SolicitudProyectoNotFoundException
 */
public class SolicitudProyectoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_SOLICITUD_PROYECTO = "org.crue.hercules.sgi.csp.model.SolicitudProyecto.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoNotFoundException(Long solicitudProyectoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_SOLICITUD_PROYECTO), solicitudProyectoId }));
  }

}
