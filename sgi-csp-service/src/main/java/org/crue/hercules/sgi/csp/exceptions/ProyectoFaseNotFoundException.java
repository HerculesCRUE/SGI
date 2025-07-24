package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ProyectoFaseNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_FASE = "org.crue.hercules.sgi.csp.model.ProyectoFase.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoFaseNotFoundException(Long proyectoFaseId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_FASE), proyectoFaseId }));
  }

}
