package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class TipoHitoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_TIPO_HITO = "org.crue.hercules.sgi.csp.model.TipoHito.message";
  private static final long serialVersionUID = 1L;

  public TipoHitoNotFoundException(Long tipoHitoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_HITO), tipoHitoId }));
  }

}
