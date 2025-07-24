package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ConvocatoriaNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_CONVOCATORIA = "org.crue.hercules.sgi.csp.model.Convocatoria.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaNotFoundException(Long convocatoriaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_CONVOCATORIA), convocatoriaId }));
  }
}
