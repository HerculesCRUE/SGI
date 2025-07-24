package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ConvocatoriaAreaTematicaNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_CONVOCATORIA_AREA_TEMATICA = "org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaAreaTematicaNotFoundException(Long convocatoriaAreaTematicaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(
            MSG_MODEL_CONVOCATORIA_AREA_TEMATICA), convocatoriaAreaTematicaId }));
  }
}
