package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ProyectoHitoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_HITO = "org.crue.hercules.sgi.csp.model.ProyectoHito.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ProyectoHitoNotFoundException(Long proyectoHitoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_HITO), proyectoHitoId }));
  }

}
