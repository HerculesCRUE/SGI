package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * TipoDocumentoNotFoundException
 */
public class TipoDocumentoNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_TIPO_DOCUMENTO = "org.crue.hercules.sgi.eti.model.TipoDocumento.message";
  /**
   * Serial Version.
   */
  private static final long serialVersionUID = 1L;

  public TipoDocumentoNotFoundException(Long tipoDocumentoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_DOCUMENTO), tipoDocumentoId }));
  }

}