package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ProyectoSocioNotFoundException
 */
public class ProyectoSocioNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_SOCIO = "org.crue.hercules.sgi.csp.model.ProyectoSocio.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoSocioNotFoundException(Long proyectoSocioId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_SOCIO), proyectoSocioId }));
  }

}
