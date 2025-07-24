package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class DocumentoRequeridoSolicitudNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_DOCUMENTO_REQUERIDO_SOLICITUD = "org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public DocumentoRequeridoSolicitudNotFoundException(Long documentoRequeridoSolicitudId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_DOCUMENTO_REQUERIDO_SOLICITUD),
            documentoRequeridoSolicitudId }));
  }
}
