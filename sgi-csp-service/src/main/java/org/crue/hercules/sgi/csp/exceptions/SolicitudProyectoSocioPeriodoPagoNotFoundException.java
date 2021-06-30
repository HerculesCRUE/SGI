package org.crue.hercules.sgi.csp.exceptions;

/**
 * SolicitudProyectoSocioPeriodoPagoNotFoundException
 */
public class SolicitudProyectoSocioPeriodoPagoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoSocioPeriodoPagoNotFoundException(Long solicitudProyectoSocioPeriodoPagoId) {
    super("Solicitud proyecto periodo pago " + solicitudProyectoSocioPeriodoPagoId + " does not exist.");
  }

}
