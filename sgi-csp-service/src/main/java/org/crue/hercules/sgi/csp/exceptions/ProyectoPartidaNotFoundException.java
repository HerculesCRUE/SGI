package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ProyectoPartidaNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_PARTIDA = "org.crue.hercules.sgi.csp.model.ProyectoPartida.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoPartidaNotFoundException(Long proyectoPartidaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_PARTIDA), proyectoPartidaId }));
  }
}
