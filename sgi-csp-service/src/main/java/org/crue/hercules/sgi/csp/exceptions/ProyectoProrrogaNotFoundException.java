package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ProyectoProrrogaNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_PRORROGA = "org.crue.hercules.sgi.csp.model.ProyectoProrroga.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ProyectoProrrogaNotFoundException(Long proyectoProrrogaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_PRORROGA), proyectoProrrogaId }));
  }

}
