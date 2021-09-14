package org.crue.hercules.sgi.csp.exceptions;

public class ProyectoPeriodoJustificacionNotFoundException extends CspNotFoundException {

  /**
   * ProyectoPeriodoJustificacionNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public ProyectoPeriodoJustificacionNotFoundException(Long proyectoPeriodoJustificacionId) {
    super("ProyectoPeriodoJustificacion " + proyectoPeriodoJustificacionId + " does not exist.");
  }
}
