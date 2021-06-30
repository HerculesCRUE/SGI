package org.crue.hercules.sgi.csp.exceptions;

public class RolProyectoColectivoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public RolProyectoColectivoNotFoundException(Long rolProyectoColectivoId) {
    super("RolProyectoColectivo " + rolProyectoColectivoId + " does not exist.");
  }
}
