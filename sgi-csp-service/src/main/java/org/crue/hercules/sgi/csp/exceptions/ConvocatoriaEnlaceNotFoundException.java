package org.crue.hercules.sgi.csp.exceptions;

/**
 * ConvocatoriaEnlaceNotFoundException
 */
public class ConvocatoriaEnlaceNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaEnlaceNotFoundException(Long convocatoriaEnlaceId) {
    super("ConvocatoriaEnlace " + convocatoriaEnlaceId + " does not exist.");
  }

}
