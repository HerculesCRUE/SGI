package org.crue.hercules.sgi.csp.exceptions;

public class ConvocatoriaFaseNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaFaseNotFoundException(Long convocatoriaFaseId) {
    super("ConvocatoriaFase " + convocatoriaFaseId + " does not exist.");
  }

}
