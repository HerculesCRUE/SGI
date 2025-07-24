package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ProyectoPeriodoSeguimientoDocumentoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO = "org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumento.message";
  /**
   * ProyectoPeriodoSeguimientoDocumentoNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public ProyectoPeriodoSeguimientoDocumentoNotFoundException(Long proyectoPeriodoSeguimientoDocumentoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(
            MSG_MODEL_PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO),
            proyectoPeriodoSeguimientoDocumentoId }));
  }
}
