package org.crue.hercules.sgi.csp.exceptions;

public class ConvocatoriaAreaTematicaNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaAreaTematicaNotFoundException(Long convocatoriaAreaTematicaId) {
    super("ConvocatoriaAreaTematica " + convocatoriaAreaTematicaId + " does not exist.");
  }
}
