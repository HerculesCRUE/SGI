package org.crue.hercules.sgi.csp.exceptions;

public class ModeloTipoFinalidadNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ModeloTipoFinalidadNotFoundException(Long modeloTipoFinalidadId) {
    super("ModeloTipoFinalidad " + modeloTipoFinalidadId + " does not exist.");
  }
}
