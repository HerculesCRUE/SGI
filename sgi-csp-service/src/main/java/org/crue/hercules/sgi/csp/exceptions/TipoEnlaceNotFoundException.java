package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class TipoEnlaceNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_TIPO_ENLACE = "org.crue.hercules.sgi.csp.model.TipoEnlace.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public TipoEnlaceNotFoundException(Long tipoEnlaceId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_ENLACE), tipoEnlaceId }));
  }
}
