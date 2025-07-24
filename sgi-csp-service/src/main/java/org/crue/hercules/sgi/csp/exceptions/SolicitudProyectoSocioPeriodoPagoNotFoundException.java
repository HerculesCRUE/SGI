package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * SolicitudProyectoSocioPeriodoPagoNotFoundException
 */
public class SolicitudProyectoSocioPeriodoPagoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_SOLICITUD_PROYECTO_PERIODO_PAGO = "org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoPago.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoSocioPeriodoPagoNotFoundException(Long solicitudProyectoSocioPeriodoPagoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_SOLICITUD_PROYECTO_PERIODO_PAGO),
            solicitudProyectoSocioPeriodoPagoId }));

  }

}
