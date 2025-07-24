package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ProyectoPaqueteTrabajoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_PAQUETE_TRABAJO = "org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoPaqueteTrabajoNotFoundException(Long proyectoPaqueteTrabajoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_PAQUETE_TRABAJO),
            proyectoPaqueteTrabajoId }));
  }

}
