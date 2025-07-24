package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ConfiguracionSolicitudNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_CONFIGURACION_SOLICITUD = "org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConfiguracionSolicitudNotFoundException(Long configuracionSolicitudId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(
            MSG_MODEL_CONFIGURACION_SOLICITUD), configuracionSolicitudId }));
  }
}
