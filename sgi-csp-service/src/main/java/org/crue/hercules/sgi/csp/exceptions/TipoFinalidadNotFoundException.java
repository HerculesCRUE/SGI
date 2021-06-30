package org.crue.hercules.sgi.csp.exceptions;

public class TipoFinalidadNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public TipoFinalidadNotFoundException(Long tipoFinalidadId) {
    super("TipoFinalidad " + tipoFinalidadId + " does not exist.");
  }
}
