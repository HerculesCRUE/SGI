package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ProyectoDocumentoNotFoundException
 */
public class ProyectoDocumentoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_DOCUMENTO = "org.crue.hercules.sgi.csp.model.ProyectoDocumento.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoDocumentoNotFoundException(Long proyectoDocumentoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_DOCUMENTO), proyectoDocumentoId }));
  }

}
