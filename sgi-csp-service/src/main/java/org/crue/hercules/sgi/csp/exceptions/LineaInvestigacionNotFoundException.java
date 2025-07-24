package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class LineaInvestigacionNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_LINEA_INVESTIGACION = "org.crue.hercules.sgi.csp.model.LineaInvestigacion.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public LineaInvestigacionNotFoundException(Long lineaInvestigacionId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_LINEA_INVESTIGACION), lineaInvestigacionId }));
  }
}
