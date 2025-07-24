package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class TipoFinalidadNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_TIPO_FINALIDAD = "org.crue.hercules.sgi.csp.model.TipoFinalidad.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public TipoFinalidadNotFoundException(Long tipoFinalidadId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_FINALIDAD), tipoFinalidadId }));
  }
}
