package org.crue.hercules.sgi.csp.exceptions;

public class ConvocatoriaPartidaNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaPartidaNotFoundException(Long convocatoriaPartidaId) {
    super("ConvocatoriaPartida " + convocatoriaPartidaId + " does not exist.");
  }
}