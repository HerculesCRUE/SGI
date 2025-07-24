package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA = "org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadora.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException(Long solicitudProyectoEntidadFinanciadoraAjenaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA),
            solicitudProyectoEntidadFinanciadoraAjenaId }));
  }
}
