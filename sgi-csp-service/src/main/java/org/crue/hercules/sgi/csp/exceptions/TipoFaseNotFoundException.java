package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class TipoFaseNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_TIPO_FASE = "org.crue.hercules.sgi.csp.model.TipoFase.message";
  private static final long serialVersionUID = 1L;

  public TipoFaseNotFoundException(Long tipoFaseId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_FASE), tipoFaseId }));
  }

}
