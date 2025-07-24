package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ModeloTipoDocumentoNotFoundException
 */
public class ModeloTipoDocumentoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_MODELO_TIPO_DOCUMENTO = "org.crue.hercules.sgi.csp.model.ModeloTipoDocumento.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ModeloTipoDocumentoNotFoundException(Long modeloTipoDocumentoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_TIPO_DOCUMENTO), modeloTipoDocumentoId }));
  }
}
