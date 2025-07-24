package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ProyectoEntidadConvocanteNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_ENTIDAD_CONVOCANTE = "org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoEntidadConvocanteNotFoundException(Long proyectoEntidadConvocanteId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_ENTIDAD_CONVOCANTE),
            proyectoEntidadConvocanteId }));
  }
}
