package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ProyectoIVANotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_IVA = "org.crue.hercules.sgi.csp.model.ProyectoIVA.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ProyectoIVANotFoundException(Long proyectoIVAId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_IVA), proyectoIVAId }));
  }

}
