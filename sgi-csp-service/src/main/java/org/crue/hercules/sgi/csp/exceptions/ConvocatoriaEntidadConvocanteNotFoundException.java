package org.crue.hercules.sgi.csp.exceptions;

public class ConvocatoriaEntidadConvocanteNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaEntidadConvocanteNotFoundException(Long convocatoriaEntidadConvocanteId) {
    super("ConvocatoriaEntidadConvocante " + convocatoriaEntidadConvocanteId + " does not exist.");
  }
}
