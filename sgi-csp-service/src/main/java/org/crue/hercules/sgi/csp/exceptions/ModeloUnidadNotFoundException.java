package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ModeloUnidadNotFoundException
 */
public class ModeloUnidadNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_MODELO_UNIDAD = "org.crue.hercules.sgi.csp.model.ModeloUnidad.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ModeloUnidadNotFoundException(Long modeloUnidadId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_UNIDAD), modeloUnidadId }));
  }
}
