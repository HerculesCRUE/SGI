package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ConvocatoriaDocumentoNotFoundException
 */
public class ConvocatoriaDocumentoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_CONVOCATORIA_DOCUMENTO = "org.crue.hercules.sgi.csp.model.ConvocatoriaDocumento.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaDocumentoNotFoundException(Long convocatoriaDocumentoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(
            MSG_MODEL_CONVOCATORIA_DOCUMENTO), convocatoriaDocumentoId }));
  }

}
