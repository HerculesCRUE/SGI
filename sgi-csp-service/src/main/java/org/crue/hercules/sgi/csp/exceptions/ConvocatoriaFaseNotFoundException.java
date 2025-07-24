package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ConvocatoriaFaseNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_CONVOCATORIA_FASE = "org.crue.hercules.sgi.csp.model.ConvocatoriaFase.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaFaseNotFoundException(Long convocatoriaFaseId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_CONVOCATORIA_FASE), convocatoriaFaseId }));
  }

}
