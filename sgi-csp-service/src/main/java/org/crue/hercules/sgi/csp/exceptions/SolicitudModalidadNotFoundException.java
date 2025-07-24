package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * SolicitudModalidadNotFoundException
 */
public class SolicitudModalidadNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_SOLICITUD_MODALIDAD = "org.crue.hercules.sgi.csp.model.SolicitudModalidad.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudModalidadNotFoundException(Long solicitudModalidadId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_SOLICITUD_MODALIDAD), solicitudModalidadId }));
  }

}
