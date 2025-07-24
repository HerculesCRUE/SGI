package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class AreaTematicaNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_AREA_TEMATICA = "org.crue.hercules.sgi.csp.model.AreaTematica.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public AreaTematicaNotFoundException(Long areaTematicaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_AREA_TEMATICA), areaTematicaId }));
  }
}
