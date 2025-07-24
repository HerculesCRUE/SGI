package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class TipoFinanciacionNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_TIPO_FINANCIACION = "org.crue.hercules.sgi.csp.model.TipoFinanciacion.message";
  private static final long serialVersionUID = 1L;

  public TipoFinanciacionNotFoundException(Long tipoFinanciacionId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_FINANCIACION), tipoFinanciacionId }));

  }
}
