package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * EquipoTrabajoNotFoundException
 */
public class EquipoTrabajoNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_EQUIPO_TRABAJO = "org.crue.hercules.sgi.eti.model.EquipoTrabajo.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public EquipoTrabajoNotFoundException(Long equipoTrabajoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_EQUIPO_TRABAJO), equipoTrabajoId }));
  }

}