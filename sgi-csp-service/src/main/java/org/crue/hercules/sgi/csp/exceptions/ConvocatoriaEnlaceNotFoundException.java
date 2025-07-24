package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ConvocatoriaEnlaceNotFoundException
 */
public class ConvocatoriaEnlaceNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_CONVOCATORIA_ENLACE = "org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaEnlaceNotFoundException(Long convocatoriaEnlaceId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_CONVOCATORIA_ENLACE), convocatoriaEnlaceId }));
  }

}
