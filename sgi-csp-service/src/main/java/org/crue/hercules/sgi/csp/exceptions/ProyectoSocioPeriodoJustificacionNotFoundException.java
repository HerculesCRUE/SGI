package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ProyectoSocioPeriodoJustificacionNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_SOCIO_PERIODO_JUSTIFICACION = "org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoSocioPeriodoJustificacionNotFoundException(Long proyectoSocioPeriodoJustificacionId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(
            MSG_MODEL_PROYECTO_SOCIO_PERIODO_JUSTIFICACION), proyectoSocioPeriodoJustificacionId }));
  }
}
