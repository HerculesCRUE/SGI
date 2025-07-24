package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ModeloTipoEnlaceNotFoundException
 */
public class ModeloTipoEnlaceNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_MODELO_TIPO_ENLACE = "org.crue.hercules.sgi.csp.model.ModeloTipoEnlace.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ModeloTipoEnlaceNotFoundException(Long modeloTipoEnlaceId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_TIPO_ENLACE), modeloTipoEnlaceId }));
  }
}
