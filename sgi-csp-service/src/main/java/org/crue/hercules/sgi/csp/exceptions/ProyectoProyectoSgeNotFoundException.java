package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ProyectoProyectoSgeNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_PROYECTO_SGE = "org.crue.hercules.sgi.csp.model.ProyectoProyectoSge.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoProyectoSgeNotFoundException(Long proyectoProyectoSgeId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_PROYECTO_SGE), proyectoProyectoSgeId }));
  }
}
