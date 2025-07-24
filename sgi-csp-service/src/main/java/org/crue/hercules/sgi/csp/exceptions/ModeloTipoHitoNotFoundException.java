package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ModeloTipoHitoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_MODELO_TIPO_HITO = "org.crue.hercules.sgi.csp.model.ModeloTipoHito.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ModeloTipoHitoNotFoundException(Long modeloTipoHitoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_TIPO_HITO), modeloTipoHitoId }));
  }
}
