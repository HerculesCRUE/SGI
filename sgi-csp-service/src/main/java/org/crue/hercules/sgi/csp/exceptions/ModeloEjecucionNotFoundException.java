package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ModeloEjecucionNotFoundException
 */
public class ModeloEjecucionNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.model.ModeloEjecucion.message";
  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public ModeloEjecucionNotFoundException(Long modeloEjecucionId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_EJECUCION), modeloEjecucionId }));
  }

}