package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ProyectoAnualidadNotFoundException
 */
public class ProyectoAnualidadNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_ANUALIDAD = "org.crue.hercules.sgi.csp.model.ProyectoAnualidad.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoAnualidadNotFoundException(Long proyectoAnualidadId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_ANUALIDAD), proyectoAnualidadId }));
  }

}
