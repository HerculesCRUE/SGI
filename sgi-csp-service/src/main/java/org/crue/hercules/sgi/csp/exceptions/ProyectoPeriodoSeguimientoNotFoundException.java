package org.crue.hercules.sgi.csp.exceptions;

public class ProyectoPeriodoSeguimientoNotFoundException extends CspNotFoundException {

  /**
   * ProyectoPeriodoSeguimientoNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public ProyectoPeriodoSeguimientoNotFoundException(Long ProyectoPeriodoSeguimientoId) {
    super("ProyectoPeriodoSeguimiento " + ProyectoPeriodoSeguimientoId + " does not exist.");
  }

}
