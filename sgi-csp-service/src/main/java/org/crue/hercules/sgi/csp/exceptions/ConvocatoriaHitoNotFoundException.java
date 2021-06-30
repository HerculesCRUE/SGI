package org.crue.hercules.sgi.csp.exceptions;

public class ConvocatoriaHitoNotFoundException extends CspNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaHitoNotFoundException(Long convocatoriaHitoId) {
    super("ConvocatoriaHito " + convocatoriaHitoId + " does not exist.");
  }

}
