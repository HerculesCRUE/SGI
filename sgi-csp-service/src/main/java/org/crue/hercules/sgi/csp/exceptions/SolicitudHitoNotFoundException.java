package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * SolicitudHitoNotFoundException
 */
public class SolicitudHitoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_SOLICITUD_HITO = "org.crue.hercules.sgi.csp.model.SolicitudHito.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudHitoNotFoundException(Long solicitudHitoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_SOLICITUD_HITO), solicitudHitoId }));
  }
}
