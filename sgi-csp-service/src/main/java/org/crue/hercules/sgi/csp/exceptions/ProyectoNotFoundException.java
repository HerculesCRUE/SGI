package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ProyectoNotFoundException
 */
public class ProyectoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO = "org.crue.hercules.sgi.csp.model.Proyecto.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoNotFoundException(Long proyectoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO), proyectoId }));
  }

}
