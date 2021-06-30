package org.crue.hercules.sgi.csp.exceptions;

public class AreaTematicaNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public AreaTematicaNotFoundException(Long areaTematicaId) {
    super("AreaTematica " + areaTematicaId + " does not exist.");
  }
}
