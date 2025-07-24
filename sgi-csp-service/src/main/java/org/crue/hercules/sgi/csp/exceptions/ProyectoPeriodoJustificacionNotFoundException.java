package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ProyectoPeriodoJustificacionNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_PERIODO_JUSTIFICACION = "org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion.message";
  /**
   * ProyectoPeriodoJustificacionNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public ProyectoPeriodoJustificacionNotFoundException(Long proyectoPeriodoJustificacionId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_PERIODO_JUSTIFICACION),
            proyectoPeriodoJustificacionId }));
  }
}
