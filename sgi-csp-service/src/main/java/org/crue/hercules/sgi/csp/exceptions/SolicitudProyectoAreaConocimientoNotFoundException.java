package org.crue.hercules.sgi.csp.exceptions;

public class SolicitudProyectoAreaConocimientoNotFoundException extends CspNotFoundException {

  /**
   * Excepcion para Area conocimiento no encontrada
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoAreaConocimientoNotFoundException(Long solicitudProyectoAreaConocimientoId) {
    super("solicitudProyectoAreaConocimiento " + solicitudProyectoAreaConocimientoId + " does not exist.");
  }
}
