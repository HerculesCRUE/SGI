package org.crue.hercules.sgi.csp.exceptions;

/**
 * SolicitudProyectoNotFoundException
 */
public class SolicitudProyectoPresupuestoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoPresupuestoNotFoundException(Long solicitudProyectoPresupuestoId) {
    super("Solicitud proyecto presupuesto " + solicitudProyectoPresupuestoId + " does not exist.");
  }

}
