package org.crue.hercules.sgi.csp.exceptions;

public class ConvocatoriaEntidadGestoraNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaEntidadGestoraNotFoundException(Long convocatoriaEntidadGestoraId) {
    super("ConvocatoriaEntidadGestora " + convocatoriaEntidadGestoraId + " does not exist.");
  }
}
