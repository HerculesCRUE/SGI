package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ConvocatoriaEntidadGestoraNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_CONVOCATORIA_ENTIDAD_GESTORA = "org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaEntidadGestoraNotFoundException(Long convocatoriaEntidadGestoraId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_CONVOCATORIA_ENTIDAD_GESTORA),
            convocatoriaEntidadGestoraId }));
  }
}
