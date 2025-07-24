package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * EstadoSolicitudNotFoundException
 */
public class EstadoSolicitudNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_ESTADO_SOLICITUD = "org.crue.hercules.sgi.csp.model.EstadoSolicitud.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public EstadoSolicitudNotFoundException(Long estadoSolicitudId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_ESTADO_SOLICITUD), estadoSolicitudId }));
  }

}
