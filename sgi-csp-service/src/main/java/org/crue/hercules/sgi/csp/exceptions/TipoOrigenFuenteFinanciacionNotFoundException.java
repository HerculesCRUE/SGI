package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * TipoOrigenFuenteFinanciacionNotFoundException
 */
public class TipoOrigenFuenteFinanciacionNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_TIPO_ORIGEN_FUENTE_FINANCIACION = "org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public TipoOrigenFuenteFinanciacionNotFoundException(Long tipoOrigenFuenteFinanciacionId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_ORIGEN_FUENTE_FINANCIACION),
            tipoOrigenFuenteFinanciacionId }));
  }
}
