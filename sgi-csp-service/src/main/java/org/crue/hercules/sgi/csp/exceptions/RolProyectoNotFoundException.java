package org.crue.hercules.sgi.csp.exceptions;

public class RolProyectoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public RolProyectoNotFoundException(Long rolProyectoId) {
    super("RolProyecto " + rolProyectoId + " does not exist.");
  }
}
