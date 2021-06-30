package org.crue.hercules.sgi.csp.exceptions;

/**
 * ProyectoSocioNotFoundException
 */
public class ProyectoSocioPeriodoPagoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoSocioPeriodoPagoNotFoundException(Long proyectoSocioPeriodoPagoId) {
    super("Proyecto socio periodo pago " + proyectoSocioPeriodoPagoId + " does not exist.");
  }

}
