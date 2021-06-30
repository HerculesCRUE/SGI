package org.crue.hercules.sgi.csp.exceptions;

public class ProyectoPartidaNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoPartidaNotFoundException(Long proyectoPartidaId) {
    super("ProyectoPartida " + proyectoPartidaId + " does not exist.");
  }
}
