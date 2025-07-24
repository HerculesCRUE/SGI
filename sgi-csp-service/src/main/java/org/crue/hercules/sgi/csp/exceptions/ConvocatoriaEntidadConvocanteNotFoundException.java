package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ConvocatoriaEntidadConvocanteNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_CONVOCATORIA_ENTIDAD_CONVOCANTE = "org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaEntidadConvocanteNotFoundException(Long convocatoriaEntidadConvocanteId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(
            MSG_MODEL_CONVOCATORIA_ENTIDAD_CONVOCANTE), convocatoriaEntidadConvocanteId }));
  }
}
