package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class SolicitudDocumentoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_SOLICITUD_DOCUMENTO = "org.crue.hercules.sgi.csp.model.SolicitudDocumento.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudDocumentoNotFoundException(Long solicitudDocumentoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_SOLICITUD_DOCUMENTO), solicitudDocumentoId }));
  }
}
