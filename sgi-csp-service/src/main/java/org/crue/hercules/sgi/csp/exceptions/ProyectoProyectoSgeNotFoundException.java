package org.crue.hercules.sgi.csp.exceptions;

public class ProyectoProyectoSgeNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoProyectoSgeNotFoundException(Long proyectoProyectoSgeId) {
    super("ProyectoProyectoSge " + proyectoProyectoSgeId + " does not exist.");
  }
}
