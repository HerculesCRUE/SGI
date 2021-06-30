package org.crue.hercules.sgi.csp.exceptions;

/**
 * SolicitudProyectoNotFoundException
 */
public class SolicitudProyectoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoNotFoundException(Long solicitudProyectoId) {
    super("Solicitud proyecto " + solicitudProyectoId + " does not exist.");
  }

}
