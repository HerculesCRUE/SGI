package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class RolProyectoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_ROL_PROYECTO = "org.crue.hercules.sgi.csp.model.RolProyecto.message";
  public static final String MSG_MODEL_ROL_PROYECTO_PRINCIPAL = "org.crue.hercules.sgi.csp.model.RolProyectoPrincipal.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public RolProyectoNotFoundException(Long rolProyectoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_ROL_PROYECTO), rolProyectoId }));
  }

  public RolProyectoNotFoundException() {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_ROL_PROYECTO_PRINCIPAL), "" }));
  }
}
