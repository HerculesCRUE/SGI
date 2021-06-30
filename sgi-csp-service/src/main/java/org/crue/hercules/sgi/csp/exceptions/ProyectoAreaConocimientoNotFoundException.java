package org.crue.hercules.sgi.csp.exceptions;

public class ProyectoAreaConocimientoNotFoundException extends CspNotFoundException {

  /**
   * Excepcion para Area conocimiento no encontrada
   */
  private static final long serialVersionUID = 1L;

  public ProyectoAreaConocimientoNotFoundException(Long proyectoAreaConocimientoId) {
    super("ProyectoAreaConocimiento " + proyectoAreaConocimientoId + " does not exist.");
  }
}
