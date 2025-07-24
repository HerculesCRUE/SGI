package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * SolicitudProyectoSocioPeriodoJustificacionNotFoundException
 */
public class SolicitudProyectoSocioPeriodoJustificacionNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_SOLICITUD_PROYECTO_SOCIO_PERIODO_JUSTIFICACION = "org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoJustificacion.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoSocioPeriodoJustificacionNotFoundException(
      Long solicitudProyectoSocioPeriodoJustificacionId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(
            MSG_MODEL_SOLICITUD_PROYECTO_SOCIO_PERIODO_JUSTIFICACION), solicitudProyectoSocioPeriodoJustificacionId }));
  }

}
