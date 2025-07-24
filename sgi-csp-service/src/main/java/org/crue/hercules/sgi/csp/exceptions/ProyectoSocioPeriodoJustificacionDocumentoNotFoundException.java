package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ProyectoSocioPeriodoJustificacionDocumentoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO = "org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionDocumento.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoSocioPeriodoJustificacionDocumentoNotFoundException(
      Long proyectoSocioPeriodoJustificacionDocumentoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(
            MSG_MODEL_PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO), proyectoSocioPeriodoJustificacionDocumentoId }));
  }
}
