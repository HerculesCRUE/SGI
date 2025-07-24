package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ConvocatoriaHitoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_CONVOCATORIA_HITO = "org.crue.hercules.sgi.csp.model.ConvocatoriaHito.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaHitoNotFoundException(Long convocatoriaHitoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_CONVOCATORIA_HITO), convocatoriaHitoId }));
  }

}
