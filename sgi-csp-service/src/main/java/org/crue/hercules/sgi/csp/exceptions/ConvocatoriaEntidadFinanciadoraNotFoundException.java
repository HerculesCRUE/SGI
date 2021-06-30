package org.crue.hercules.sgi.csp.exceptions;

public class ConvocatoriaEntidadFinanciadoraNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaEntidadFinanciadoraNotFoundException(Long convocatoriaEntidadFinanciadoraId) {
    super("ConvocatoriaEntidadFinanciadora " + convocatoriaEntidadFinanciadoraId + " does not exist.");
  }
}
