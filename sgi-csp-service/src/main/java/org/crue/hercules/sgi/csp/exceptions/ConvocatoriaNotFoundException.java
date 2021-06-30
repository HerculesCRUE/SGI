package org.crue.hercules.sgi.csp.exceptions;

public class ConvocatoriaNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaNotFoundException(Long convocatoriaId) {
    super("Convocatoria " + convocatoriaId + " does not exist.");
  }
}
