package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ProyectoEntidadGestoraNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_ENTIDAD_GESTORA = "org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoEntidadGestoraNotFoundException(Long proyectoEntidadGestoraId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(
            MSG_MODEL_PROYECTO_ENTIDAD_GESTORA), proyectoEntidadGestoraId }));
  }
}
