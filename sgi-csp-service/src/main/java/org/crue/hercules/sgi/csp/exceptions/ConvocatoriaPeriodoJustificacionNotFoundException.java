package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ConvocatoriaPeriodoJustificacionNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_CONVOCATORIA_PERIODO_JUSTIFICACION = "org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaPeriodoJustificacionNotFoundException(Long convocatoriaPeriodoJustificacionId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_CONVOCATORIA_PERIODO_JUSTIFICACION),
            convocatoriaPeriodoJustificacionId }));
  }
}
