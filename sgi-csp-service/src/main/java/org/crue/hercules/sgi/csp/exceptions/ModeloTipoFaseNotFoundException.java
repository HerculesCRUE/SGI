package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ModeloTipoFaseNotFoundException
 */
public class ModeloTipoFaseNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_MODELO_TIPO_FASE = "org.crue.hercules.sgi.csp.model.ModeloTipoFase.message";
  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public ModeloTipoFaseNotFoundException(Long modeloTipoFaseId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_TIPO_FASE), modeloTipoFaseId }));
  }

}
