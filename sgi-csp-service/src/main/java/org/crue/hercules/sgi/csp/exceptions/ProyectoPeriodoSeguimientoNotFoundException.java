package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ProyectoPeriodoSeguimientoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_PERIODO_SEGUIMIENTO = "org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento.message";
  /**
   * ProyectoPeriodoSeguimientoNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public ProyectoPeriodoSeguimientoNotFoundException(Long proyectoPeriodoSeguimientoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(
            MSG_MODEL_PROYECTO_PERIODO_SEGUIMIENTO), proyectoPeriodoSeguimientoId }));
  }

}
