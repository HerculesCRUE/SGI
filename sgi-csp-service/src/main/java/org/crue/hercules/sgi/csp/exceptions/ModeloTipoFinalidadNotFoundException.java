package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ModeloTipoFinalidadNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_MODELO_TIPO_FINALIDAD = "org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ModeloTipoFinalidadNotFoundException(Long modeloTipoFinalidadId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_TIPO_FINALIDAD), modeloTipoFinalidadId }));
  }
}
