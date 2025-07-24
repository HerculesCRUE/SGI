package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ConvocatoriaPartidaNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_CONVOCATORIA_PARTIDA = "org.crue.hercules.sgi.csp.model.ConvocatoriaPartida.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaPartidaNotFoundException(Long convocatoriaPartidaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_CONVOCATORIA_PARTIDA), convocatoriaPartidaId }));
  }
}