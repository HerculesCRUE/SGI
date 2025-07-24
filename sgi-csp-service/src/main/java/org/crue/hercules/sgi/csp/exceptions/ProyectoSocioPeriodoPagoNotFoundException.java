package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ProyectoSocioNotFoundException
 */
public class ProyectoSocioPeriodoPagoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_SOCIO_PERIODO_PAGO = "org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoPago.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoSocioPeriodoPagoNotFoundException(Long proyectoSocioPeriodoPagoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_SOCIO_PERIODO_PAGO),
            proyectoSocioPeriodoPagoId }));
  }

}
