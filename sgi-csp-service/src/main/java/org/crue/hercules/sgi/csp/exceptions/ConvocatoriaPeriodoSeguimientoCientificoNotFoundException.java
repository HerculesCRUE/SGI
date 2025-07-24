package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ConvocatoriaPeriodoSeguimientoCientificoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_CONV_PERIODO_SEG_CIENTIFICO = "org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaPeriodoSeguimientoCientificoNotFoundException(Long convocatoriaPeriodoSeguimientoCientificoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(
            MSG_MODEL_CONV_PERIODO_SEG_CIENTIFICO),
            convocatoriaPeriodoSeguimientoCientificoId }));
  }
}
